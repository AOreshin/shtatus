package com.github.aoreshin.connectivity.viewmodels

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class DeletingViewModel  @Inject constructor(
    private val connectionRepository: ConnectionRepository
) : ViewModel() {

    fun delete(id: Int) {
        connectionRepository.delete(id)
    }
}