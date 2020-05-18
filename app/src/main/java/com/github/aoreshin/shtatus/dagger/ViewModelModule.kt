package com.github.aoreshin.shtatus.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.shtatus.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionListViewModel::class)
    internal abstract fun connectionListViewModel(viewModel: ConnectionListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddingViewModel::class)
    internal abstract fun addingViewModel(viewModel: AddingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditingViewModel::class)
    internal abstract fun editingViewModel(viewModel: EditingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DeletingViewModel::class)
    internal abstract fun deletingViewModel(viewModel: DeletingViewModel): ViewModel
}