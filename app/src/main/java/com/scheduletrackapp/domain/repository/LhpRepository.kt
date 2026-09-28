package com.scheduletrackapp.domain.repository

import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus
import kotlinx.coroutines.flow.Flow

interface LhpRepository {
    fun getLhpRecords() : Flow<List<LhpRecord>>
    fun getLhpById(id: String) : Flow<LhpRecord?>
    suspend fun createLhpRecord(record: LhpRecord) : Result<Unit>
    suspend fun updateLhpStatus(id: String, newStatus: LhpStatus, notes: String? = null) : Result<Unit>
    suspend fun deleteLhpRecord(id: String): Result<Unit>
}