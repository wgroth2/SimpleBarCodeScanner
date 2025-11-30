/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.digiroth.simplebarcodescanner.data.Scan
import com.digiroth.simplebarcodescanner.data.ScanHistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Scan History screen.
 *
 * It retrieves and manages the list of historical scans from the [ScanHistoryRepository]
 * and exposes it to the UI as a [StateFlow].
 *
 * @param repository The repository for accessing scan history data.
 */
class ScanHistoryViewModel(private val repository: ScanHistoryRepository) : ViewModel() {

    /**
     * A [StateFlow] that holds the current list of all historical scans.
     * The UI layer can collect this flow to observe data changes.
     */
    val allScans: StateFlow<List<Scan>> = repository.allScans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Deletes all scans from the history.
     */
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}

/**
 * Factory for creating instances of [ScanHistoryViewModel].
 *
 * This is necessary because [ScanHistoryViewModel] has a non-empty constructor
 * that requires a [ScanHistoryRepository] instance.
 *
 * @param repository The repository to be provided to the ViewModel.
 */
class ScanHistoryViewModelFactory(private val repository: ScanHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
