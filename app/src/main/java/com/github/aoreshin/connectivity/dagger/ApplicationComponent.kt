package com.github.aoreshin.connectivity.dagger

import android.app.Application
import com.github.aoreshin.connectivity.*
import com.github.aoreshin.connectivity.dialogs.AddConnectionDialogFragment
import com.github.aoreshin.connectivity.dialogs.DeletingDialogFragment
import com.github.aoreshin.connectivity.dialogs.EditingDialogFragment
import com.github.aoreshin.connectivity.room.ConnectionDao
import com.github.aoreshin.connectivity.room.ConnectivityDatabase
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, RoomModule::class, RetrofitModule::class])
interface ApplicationComponent {
    fun inject(activity: AddConnectionDialogFragment)
    fun inject(fragment: ConnectionListFragment)
    fun inject(fragment: DeletingDialogFragment)
    fun inject(fragment: EditingDialogFragment)

    fun connectionDao(): ConnectionDao?

    fun database(): ConnectivityDatabase?

    fun application(): Application?

    fun retrofitService(): RetrofitService?
}