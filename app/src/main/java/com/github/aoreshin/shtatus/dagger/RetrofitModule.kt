package com.github.aoreshin.shtatus.dagger

import com.github.aoreshin.shtatus.viewmodels.RetrofitService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
class RetrofitModule {
    private val retrofit = Retrofit
        .Builder()
        .baseUrl("http://localhost/")
        .client(OkHttpClient()
            .newBuilder()
            .callTimeout(Duration.ofSeconds(3))
            .build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Singleton
    @Provides
    fun retrofit(): Retrofit {
        return retrofit
    }

    @Singleton
    @Provides
    fun retrofitService(): RetrofitService {
        return retrofit.create(RetrofitService::class.java)
    }
}