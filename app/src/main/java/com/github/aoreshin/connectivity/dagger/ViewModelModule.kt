package com.github.aoreshin.connectivity.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.connectivity.viewmodels.AddConnectionDialogFragmentViewModel
import com.github.aoreshin.connectivity.viewmodels.ApplicationViewModel
import com.github.aoreshin.connectivity.viewmodels.ViewModelFactory
import com.github.aoreshin.connectivity.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ApplicationViewModel::class)
    internal abstract fun connectionsViewModel(viewModel: ApplicationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddConnectionDialogFragmentViewModel::class)
    internal abstract fun addFragmentViewModel(viewModel: AddConnectionDialogFragmentViewModel): ViewModel
}