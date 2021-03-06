package com.github.aoreshin.shtatus.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aoreshin.shtatus.events.SingleLiveEvent
import com.github.aoreshin.shtatus.room.Connection
import io.reactivex.FlowableSubscriber
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.function.Predicate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionListViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository
) : ViewModel() {
    private var tableStatus = TableStatus.OK

    private val connections = connectionRepository.allConnections()
    private val mediatorConnection = MediatorLiveData<List<Connection>>()

    private val stopRefreshingEvent = SingleLiveEvent<Void>()
    private val noMatchesEvent = SingleLiveEvent<Void>()
    private val emptyTableEvent = SingleLiveEvent<Void>()

    private val nameLiveData = MutableLiveData<String>()
    private val urlLiveData = MutableLiveData<String>()
    private val statusLiveData = MutableLiveData<String>()

    init {
        with(mediatorConnection) {
            addSource(connections) { update() }
            addSource(nameLiveData) { update() }
            addSource(urlLiveData) { update() }
            addSource(statusLiveData) { update() }
        }
    }

    fun getStopRefreshingEvent(): LiveData<Void> = stopRefreshingEvent
    fun getNoMatchesEvent(): LiveData<Void> = noMatchesEvent
    fun getEmptyTableEvent(): LiveData<Void> = emptyTableEvent
    fun getConnections(): LiveData<List<Connection>> = mediatorConnection
    fun getName(): String? = nameLiveData.value
    fun getUrl(): String? = urlLiveData.value
    fun getStatus(): String? = statusLiveData.value
    fun setName(name: String) { nameLiveData.value = name }
    fun setUrl(url: String) { urlLiveData.value = url }
    fun setStatus(status: String) { statusLiveData.value = status }

    private fun update() {
        if (connections.value != null) {
            if (connections.value.isNullOrEmpty()) {
                if (tableStatus != TableStatus.EMPTY) {
                    emptyTableEvent.call()
                    tableStatus = TableStatus.EMPTY
                }
            } else {
                mediatorConnection.value = connections.value?.filter { connection -> getPredicate().test(connection) }

                if (mediatorConnection.value.isNullOrEmpty()) {
                    if (tableStatus != TableStatus.NO_MATCHES) {
                        noMatchesEvent.call()
                        tableStatus = TableStatus.NO_MATCHES
                    }
                } else {
                    tableStatus = TableStatus.OK
                }
            }
        }
    }

    fun send() {
        if (!connections.value.isNullOrEmpty()) {
            val singles = connections.value?.map { connection ->
                val id = connection.id
                val description = connection.description
                val url = connection.url
                var message = ""

                connectionRepository.sendRequest(url)
                    .doOnSuccess { message = it.code().toString() }
                    .doOnError { message = it.message!! }
                    .doFinally {
                        val result = Connection(id, description, url, message)
                        connectionRepository.insert(result)
                    }
            }

            Single.mergeDelayError(singles)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { stopRefreshingEvent.call() }
                .subscribe(getSubscriber())
        } else {
            stopRefreshingEvent.call()
        }
    }

    private fun getSubscriber() : FlowableSubscriber<Response<ResponseBody>> {
        return object: DisposableSubscriber<Response<ResponseBody>>() {
            override fun onComplete() { Log.d(TAG, "All requests sent") }
            override fun onNext(t: Response<ResponseBody>?) { Log.d(TAG, "Request is done") }
            override fun onError(t: Throwable?) { Log.d(TAG, t!!.message!!) }
        }
    }

    private fun getPredicate(): Predicate<Connection> {
        return Predicate { connection ->
            connection.description.contains(nameLiveData.value.toString(), ignoreCase = true)
                    && connection.url.contains(urlLiveData.value.toString(), ignoreCase = true)
                    && connection.actualStatusCode.contains(
                statusLiveData.value.toString(),
                ignoreCase = true
            )
        }
    }

    private enum class TableStatus {
        NO_MATCHES,
        EMPTY,
        OK
    }

    companion object {
        private const val TAG = "ConnectionListViewModel"
    }
}