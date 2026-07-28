package com.trackapp.smsexpensetracker.smsingestion.presentation

import androidx.lifecycle.ViewModel
import com.trackapp.smsexpensetracker.smsingestion.di.ApplicationScope
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportProgress
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportWindow
import com.trackapp.smsexpensetracker.smsingestion.domain.service.HistoricalSmsImportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val historicalSmsImportService: HistoricalSmsImportService,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val _progress = MutableStateFlow(ImportProgress())
    val progress: StateFlow<ImportProgress> = _progress.asStateFlow()

    private var started = false

    /**
     * Launches on the application-scoped CoroutineScope (per ADR-001), not viewModelScope, so
     * the import survives the user navigating away before it finishes.
     */
    fun startImportIfNeeded() {
        if (started) return
        started = true
        applicationScope.launch {
            historicalSmsImportService.importSince(ImportWindow.last6Months()) { progress ->
                _progress.value = progress
            }
        }
    }
}
