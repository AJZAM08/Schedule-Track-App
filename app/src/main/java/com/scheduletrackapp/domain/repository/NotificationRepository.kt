package com.scheduletrackapp.domain.repository

import com.scheduletrackapp.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationItem>>
    fun getNotificationById(id: String): Flow<NotificationItem?>
    suspend fun markAsRead(id: String): Result<Unit>
}