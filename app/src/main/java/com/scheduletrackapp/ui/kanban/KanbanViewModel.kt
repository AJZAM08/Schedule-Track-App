package com.scheduletrackapp.ui.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scheduletrackapp.domain.model.K3Category
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus
import com.scheduletrackapp.domain.repository.LhpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KanbanViewModel (
    private val repository: LhpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KanbanUiState(isLoading = true))
    val uiState: StateFlow<KanbanUiState> = _uiState.asStateFlow()

    init {
        loadLhpRecords()
    }

    fun loadLhpRecords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getLhpRecords()
                .catch { exception ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, records = list) }
                }
        }
    }

    fun createLhpRecord(
        lhpNumber: String,
        companyName: String,
        category: K3Category,
        unitDescription: String,
        inspectionDate: String,
        clientDeadline: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val newRecord = LhpRecord(
                lhpNumber = lhpNumber,
                clientCompanyId = "",
                companyName = companyName,
                category = category,
                unitDescription = unitDescription,
                currentStatus = LhpStatus.SCHEDULED,
                inspectionDate = inspectionDate,
                clientDeadline = clientDeadline
            )

            repository.createLhpRecord(newRecord)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadLhpRecords() // 👈 Refresh otomatis data dari Supabase seketika!
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun filterByCategory(category: K3Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onInitiateStatusUpdate(record: LhpRecord, targetStatus: LhpStatus) {
        if (record.currentStatus == targetStatus) return
        _uiState.update { it.copy(pendingStatusUpdate = PendingStatusUpdate(record, targetStatus)) }
    }

    fun onConfirmStatusUpdate() {
        val pending = _uiState.value.pendingStatusUpdate ?: return
        viewModelScope.launch {
            repository.updateLhpStatus(pending.record.id, pending.targetStatus)
                .onSuccess {
                    _uiState.update { it.copy(pendingStatusUpdate = null) }
                    loadLhpRecords()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(pendingStatusUpdate = null, errorMessage = error.message)
                    }
                }
        }
    }

    fun onCancelStatusUpdate() {
        _uiState.update { it.copy(pendingStatusUpdate = null) }
    }
}