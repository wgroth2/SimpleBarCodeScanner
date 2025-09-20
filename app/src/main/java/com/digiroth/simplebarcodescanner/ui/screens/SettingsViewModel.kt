/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.digiroth.simplebarcodescanner.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    // Expose the setting as a StateFlow so the UI can collect it
    val isAutoZoomEnabled: StateFlow<Boolean> = repository.isAutoZoomEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true // Initial default value
        )

    fun updateAutoZoom(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setAutoZoomEnabled(isEnabled)
        }
    }
}

// Factory to provide the repository to the ViewModel
class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
