package com.scheduletrackapp.ui.kanban

import com.scheduletrackapp.domain.model.K3Category
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus

data class PendingStatusUpdate(
    val record: LhpRecord,
    val targetStatus: LhpStatus
)

data class KanbanUiState(
    val isLoading: Boolean = false,
    val records: List<LhpRecord> = emptyList(),
    val selectedCategory: K3Category? = null,
    val pendingStatusUpdate: PendingStatusUpdate? = null,
    val errorMessage: String? = null
)