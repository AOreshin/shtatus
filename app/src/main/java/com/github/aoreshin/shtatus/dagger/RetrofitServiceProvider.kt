package com.github.aoreshin.shtatus.dagger

import com.github.aoreshin.shtatus.viewmodels.RetrofitService

data class RetrofitServiceProvider(var retrofitService: RetrofitService, private val retrofitModule: RetrofitModule)