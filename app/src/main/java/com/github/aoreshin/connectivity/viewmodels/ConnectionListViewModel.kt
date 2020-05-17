package com.github.aoreshin.connectivity.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aoreshin.connectivity.events.SingleLiveEvent
import com.github.aoreshin.connectivity.room.Connection
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.function.Predicate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionListViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val connections = connectionRepository.allConnections()

    val mediatorConnection = MediatorLiveData<List<Connection>>().also {
        it.value = connections.value
    }

    val refreshLiveData = MutableLiveData(RefreshStatus.READY)
    val noMatchesEvent = SingleLiveEvent<Void>()
    val emptyTableEvent = SingleLiveEvent<Void>()

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
    }

    private fun update() {
        if (connections.value.isNullOrEmpty()) {
            emptyTableEvent.call()
        } else {
            mediatorConnection.value = connections.value?.filter { connection -> getPredicate().test(connection) }

            if (mediatorConnection.value.isNullOrEmpty()) {
                noMatchesEvent.call()
            }
        }
    }

    fun send() {
        refreshLiveData.value = RefreshStatus.LOADING

        if (!connections.value.isNullOrEmpty()) {
            val singles = connections.value?.map { connection ->
                val id = connection.id
                val description = connection.description
                val url = connection.url
                var message = ""

                connectionRepository
                    .sendRequest(url)
                    .doOnSuccess { message = it.code().toString() }
                    .doOnError { message = it.message!! }
                    .doFinally {
                        val result = Connection(id, description, url, message)
                        connectionRepository.insert(result)
                    }
            }

            val disposable = Single.mergeDelayError(singles)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { refreshLiveData.value = RefreshStatus.READY }
                .subscribe({ Log.d("ApplicationViewModel", "Request sending is finished") },
                    { Log.d("ApplicationViewModel", it.message!!) })

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
}