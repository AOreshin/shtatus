package com.github.aoreshin.shtatus.viewmodels

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.github.aoreshin.shtatus.dagger.RetrofitModule
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
    private val application: Application,
    private val connectionDao: ConnectionDao,
    private var retrofitService: RetrofitService
) : SharedPreferences.OnSharedPreferenceChangeListener {

    init {
        PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this)
    }

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
        return retrofitService.get(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        retrofitService = RetrofitModule(application).retrofitService()
    }
}