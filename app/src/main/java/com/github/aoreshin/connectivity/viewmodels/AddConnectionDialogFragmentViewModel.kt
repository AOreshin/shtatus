package com.github.aoreshin.connectivity.viewmodels

import androidx.lifecycle.ViewModel
import com.github.aoreshin.connectivity.room.ConnectionDao
import javax.inject.Inject

class AddConnectionDialogFragmentViewModel  @Inject constructor(
    private val connectionDao: ConnectionDao) : ViewModel() {

}