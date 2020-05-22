package com.github.aoreshin.shtatus.dagger

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.aoreshin.shtatus.SettingsActivity.SettingsFragment.Companion.HTTPS_KEY
import com.github.aoreshin.shtatus.SettingsActivity.SettingsFragment.Companion.TIMEOUT_KEY
import com.github.aoreshin.shtatus.viewmodels.RetrofitService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
class RetrofitModule(application: Application) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://localhost/")
            .client(getClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun getClient(): OkHttpClient {
        val timeout = preferences.getInt(TIMEOUT_KEY, 5000).toLong()

        Log.d(TAG, "Setting $TIMEOUT_KEY $timeout")

        val client = OkHttpClient()
            .newBuilder()
            .callTimeout(Duration.ofMillis(timeout))

        val enableHttps = preferences.getBoolean(HTTPS_KEY, true)

        Log.d(TAG, "Setting $HTTPS_KEY $enableHttps")

        if (!enableHttps) {
            client.hostnameVerifier { _, _ -> true }
        }


        return client.build()
    }

    @Singleton
    @Provides
    fun retrofit(): Retrofit {
        return getRetrofit()
    }

    @Singleton
    @Provides
    fun retrofitService(): RetrofitService {
        return getRetrofit().create(RetrofitService::class.java)
    }

    companion object {
        private const val TAG = "RetrofitModule"
    }
}