package com.github.aoreshin.connectivity.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aoreshin.connectivity.room.Connection
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.function.Predicate
import javax.inject.Inject

class ConnectionListViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val connections = connectionRepository.allConnections()
    val mediatorConnection = MediatorLiveData<List<Connection>>()

    val statusLiveData = MutableLiveData(TableStatus.SHOW)
    val refreshLiveData = MutableLiveData(RefreshStatus.READY)

    val nameLiveData = MutableLiveData<String>()
    val urlLiveData = MutableLiveData<String>()
    val actualStatusLiveData = MutableLiveData<String>()

    init {
        with(mediatorConnection) {
            addSource(connections) { update() }
            addSource(nameLiveData) { update() }
            addSource(urlLiveData) { update() }
            addSource(actualStatusLiveData) { update() }
        }

        update()
    }

    private fun update() {
        if (connections.value.isNullOrEmpty()) {
            statusLiveData.value = TableStatus.EMPTY
        } else {
            val filtered =
                connections.value?.filter { connection -> getPredicate().test(connection) }

            if (filtered.isNullOrEmpty()) {
                statusLiveData.value = TableStatus.NO_MATCHES
            } else {
                mediatorConnection.value =
                    connections.value?.filter { connection -> getPredicate().test(connection) }
                statusLiveData.value = TableStatus.SHOW
            }
        }
    }

    fun send() {
        refreshLiveData.value = RefreshStatus.LOADING

        if (!connections.value.isNullOrEmpty()) {
            val singles = connections.value?.map { connection ->
                connectionRepository
                    .sendRequest(connection.url)
                    .doOnSuccess { connection.actualStatusCode = it.code().toString() }
                    .doOnError { connection.actualStatusCode = it.message!! }
                    .doFinally { connectionRepository.insert(connection) }
            }

            val disposable = Single.mergeDelayError(singles)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { refreshLiveData.value = RefreshStatus.READY }
                .subscribe({
                    Log.d(
                        "ApplicationViewModel", "Request sending is finished"
                    )
                }, {
                    Log.d("ApplicationViewModel", it.message!!)
                })

            compositeDisposable.add(disposable)
        } else {
            refreshLiveData.value = RefreshStatus.READY
        }
    }

    private fun getPredicate(): Predicate<Connection> {
        return Predicate { connection ->
            connection.description.contains(nameLiveData.value.toString(), ignoreCase = true)
                    && connection.url.contains(urlLiveData.value.toString(), ignoreCase = true)
                    && connection.actualStatusCode.contains(
                actualStatusLiveData.value.toString(),
                ignoreCase = true
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    enum class RefreshStatus {
        LOADING,
        READY
    }

    enum class TableStatus {
        SHOW,
        EMPTY,
        NO_MATCHES
    }
}