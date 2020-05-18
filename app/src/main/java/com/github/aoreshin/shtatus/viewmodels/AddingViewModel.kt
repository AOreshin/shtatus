package com.github.aoreshin.shtatus.viewmodels

import androidx.lifecycle.ViewModel
import com.github.aoreshin.shtatus.room.Connection
import javax.inject.Inject

class AddingViewModel  @Inject constructor(
    private val connectionRepository: ConnectionRepository
) : ViewModel() {

    fun save(connection: Connection) {
        connectionRepository.insert(connection)
    }
}