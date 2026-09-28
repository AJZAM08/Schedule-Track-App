package com.scheduletrackapp.domain.model

data class LhpRecord(
    val id: String = "",
    val lhpNumber: String,
    val clientCompanyId: String,
    val companyName: String = "",
    val category: K3Category,
    val unitDescription: String,
    val currentStatus: LhpStatus = LhpStatus.SCHEDULED,
    val inspectionDate: String,
    val clientDeadline: String,
    val deadlineStatus: DeadlineStatus = DeadlineStatus.SAFE,
    val inspectorId: String? = null,
    val reviewerId: String? = null,
    val notes: String? = null
)