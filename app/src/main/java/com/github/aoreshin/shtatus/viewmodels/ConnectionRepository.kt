package com.github.aoreshin.shtatus.viewmodels

import androidx.lifecycle.LiveData
import com.github.aoreshin.shtatus.dagger.RetrofitServiceProvider
import com.github.aoreshin.shtatus.room.Connection
import com.github.aoreshin.shtatus.room.ConnectionDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepository @Inject constructor(
    private val connectionDao: ConnectionDao,
    private val retrofitServiceProvider: RetrofitServiceProvider
) {

    fun allConnections(): LiveData<List<Connection>> {
        return connectionDao.all()
    }

    fun findConnection(id: Int): LiveData<Connection> {
        return connectionDao.find(id)
    }

    fun insert(connection: Connection) {
        Completable.fromAction { connectionDao.insert(connection) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun delete(id: Int) {
        Completable
            .fromAction { connectionDao.delete(id) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun sendRequest(url: String): Single<Response<ResponseBody>> {
        return retrofitServiceProvider.retrofitService.get(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}