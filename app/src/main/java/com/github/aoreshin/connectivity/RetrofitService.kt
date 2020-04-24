package com.github.aoreshin.connectivity

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitService {
    @GET
    fun get(@Url url: String): Single<Response<ResponseBody>>
}