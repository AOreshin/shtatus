package com.github.aoreshin.connectivity.dagger

import android.app.Application
import com.github.aoreshin.connectivity.fragments.AddingDialogFragment
import com.github.aoreshin.connectivity.fragments.ConnectionListFragment
import com.github.aoreshin.connectivity.fragments.DeletingDialogFragment
import com.github.aoreshin.connectivity.fragments.EditingDialogFragment
import com.github.aoreshin.connectivity.room.ConnectionDao
import com.github.aoreshin.connectivity.room.ConnectivityDatabase
import com.github.aoreshin.connectivity.viewmodels.RetrofitService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RoomModule::class, RetrofitModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(activity: AddingDialogFragment)
    fun inject(fragment: ConnectionListFragment)
    fun inject(fragment: DeletingDialogFragment)
    fun inject(fragment: EditingDialogFragment)

    fun connectionDao(): ConnectionDao?

    fun database(): ConnectivityDatabase?

    fun application(): Application?

    fun retrofitService(): RetrofitService?
}