package com.github.aoreshin.shtatus.dagger

import android.app.Application
import android.content.Context
import com.github.aoreshin.shtatus.SettingsActivity.Companion.SHTATUS_PREFS
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
    private val preferences = application.getSharedPreferences(SHTATUS_PREFS, Context.MODE_PRIVATE)

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://localhost/")
            .client(getClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun getClient(): OkHttpClient {
        val client = OkHttpClient()
            .newBuilder()
            .callTimeout(Duration.ofMillis(preferences.getLong("timeout", 5000)))

        val enableHttps = preferences.getBoolean("enableHttps", true)

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
}