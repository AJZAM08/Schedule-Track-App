package com.scheduletrackapp.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.scheduletrackapp.data.remote.supabaseClient
import com.scheduletrackapp.data.repository.LhpRepositoryImpl
import com.scheduletrackapp.domain.model.DeadlineStatus
import kotlinx.coroutines.flow.first

class SlaDeadlineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        val repository = LhpRepositoryImpl(supabaseClient)

        try {
            val records = repository.getLhpRecords().first()

            records.forEachIndexed { index, record ->
                when (record.deadlineStatus) {
                    DeadlineStatus.OVERDUE -> {
                        notificationHelper.showSlaNotification(
                            title = "🚨 DEADLINE TERLEWAT: ${record.lhpNumber}",
                            message = "Berkas ${record.companyName} (${record.unitDescription}) melewati tenggat waktu!",
                            notificationId = index + 100
                        )
                    }
                    DeadlineStatus.WARNING_NEAR_DEADLINE -> {
                        notificationHelper.showSlaNotification(
                            title = "🟧 MENJELANG DEADLINE: ${record.lhpNumber}",
                            message = "Berkas ${record.companyName} mendekati deadline ${record.clientDeadline}. Segera proses!",
                            notificationId = index + 200
                        )
                    }
                    DeadlineStatus.SAFE -> {}
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}