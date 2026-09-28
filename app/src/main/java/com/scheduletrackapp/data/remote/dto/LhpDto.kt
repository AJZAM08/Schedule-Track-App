package com.scheduletrackapp.data.remote.dto

import com.scheduletrackapp.domain.model.DeadlineStatus
import com.scheduletrackapp.domain.model.K3Category
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Serializable
data class LhpDto(
    val id: String? = null,
    @SerialName("lhp_number")
    val lhpNumber: String,
    @SerialName("client_company_id")
    val clientCompanyId: String,
    val category: String,
    @SerialName("unit_description")
    val unitDescription: String,
    @SerialName("current_status")
    val currentStatus: String,
    @SerialName("inspection_date")
    val inspectionDate: String,
    @SerialName("client_deadline")
    val clientDeadline: String,
    @SerialName("inspector_id")
    val inspectorId: String? = null,
    @SerialName("reviewer_id")
    val reviewerId: String? = null,
    val notes: String? = null
)

// Mapper DTO Supabase -> Domain Model
fun LhpDto.toDomain(companyNameMap: Map<String, String> = emptyMap()): LhpRecord {
    val deadlineStatus = calculateDeadlineStatus(clientDeadline)
    return LhpRecord(
        id = id.orEmpty(),
        lhpNumber = lhpNumber,
        clientCompanyId = clientCompanyId,
        companyName = companyNameMap[clientCompanyId].orEmpty(),
        category = try {
            K3Category.valueOf(category.trim().uppercase())
        } catch (e: Exception) {
            K3Category.PESAWAT_ANGKAT_ANGKUT
        },
        unitDescription = unitDescription,
        currentStatus = try {
            LhpStatus.valueOf(currentStatus.trim().uppercase())
        } catch (e: Exception) {
            LhpStatus.SCHEDULED
        },
        inspectionDate = inspectionDate,
        clientDeadline = clientDeadline,
        deadlineStatus = deadlineStatus,
        inspectorId = inspectorId,
        reviewerId = reviewerId,
        notes = notes
    )
}

// Mapper Domain Model -> DTO Supabase
fun LhpRecord.toDto(): LhpDto {
    return LhpDto(
        id = id.ifEmpty { null },
        lhpNumber = lhpNumber,
        clientCompanyId = clientCompanyId,
        category = category.name,
        unitDescription = unitDescription,
        currentStatus = currentStatus.name,
        inspectionDate = inspectionDate,
        clientDeadline = clientDeadline,
        inspectorId = inspectorId,
        reviewerId = reviewerId,
        notes = notes
    )
}

private fun calculateDeadlineStatus(deadlineStr: String): DeadlineStatus {
    return try {
        val today = LocalDate.now()
        val deadline = LocalDate.parse(deadlineStr)
        val daysUntil = ChronoUnit.DAYS.between(today, deadline)
        when {
            daysUntil < 0 -> DeadlineStatus.OVERDUE
            daysUntil <= 3 -> DeadlineStatus.WARNING_NEAR_DEADLINE
            else -> DeadlineStatus.SAFE
        }
    } catch (e: Exception) {
        DeadlineStatus.SAFE
    }
}