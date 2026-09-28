package com.scheduletrackapp.domain.model

enum class NotificationType {
    DEADLINE_OVERDUE,
    DEADLINE_WARNING,
    STATUS_CHANGED,
    SYSTEM_INFO
}

data class NotificationItem(
    val id: String = "",
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.SYSTEM_INFO,
    val lhpRecordId: String? = null
)