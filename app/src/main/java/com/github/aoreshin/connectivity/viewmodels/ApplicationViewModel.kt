package com.github.aoreshin.connectivity.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aoreshin.connectivity.RetrofitService
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.room.ConnectionDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ApplicationViewModel @Inject constructor(
    private val connectionDao: ConnectionDao,
    private val retrofitService: RetrofitService
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val connections: MutableLiveData<List<Connection>> by lazy {
        MutableLiveData<List<Connection>>().also {
            loadConnections()
        }
    }

    fun getConnections(): LiveData<List<Connection>> {
        return connections
    }

    fun loadConnections() {
        val disposable = connectionDao
            .all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { sendRequests(it) }

        compositeDisposable.add(disposable)
    }

    fun addConnection(connection: Connection) {
        val disposable = Completable
            .fromAction { connectionDao.insert(connection) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        compositeDisposable.add(disposable)
    }

    fun findConnection(id: Int): LiveData<Connection> {
        return connectionDao.find(id)
    }

    fun insert(connection: Connection) {
        val disposable = Completable.fromAction { connectionDao.insert(connection) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        compositeDisposable.add(disposable)
    }

    fun delete(id: Int) {
        val disposable = Completable
            .fromAction { connectionDao.delete(id) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        compositeDisposable.add(disposable)
    }

    private fun sendRequests(connections: List<Connection>) {
        val observables = connections.map { connection ->
                retrofitService.get(connection.url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { connection.actualStatusCode = it.message!! }
                    .doOnSuccess { connection.actualStatusCode = it.code().toString() }
            }

        val disposable = Single.mergeDelayError(observables)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { this.connections.value = connections }
            .subscribe({
                    Log.d(TAG, "Request sending is finished"
                )}, {
                    Log.d(TAG, it.message!!)
            })

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "ConnectionsViewModel"
    }
}

