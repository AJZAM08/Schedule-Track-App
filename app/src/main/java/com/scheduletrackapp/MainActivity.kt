package com.scheduletrackapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.scheduletrackapp.data.notification.SlaDeadlineWorker
import com.scheduletrackapp.data.remote.supabaseClient
import com.scheduletrackapp.data.repository.ClientCompanyRepositoryImpl
import com.scheduletrackapp.data.repository.LhpRepositoryImpl
import com.scheduletrackapp.domain.model.DeadlineStatus
import com.scheduletrackapp.domain.model.NotificationItem
import com.scheduletrackapp.domain.model.NotificationType
import com.scheduletrackapp.ui.company.ClientCompanyViewModel
import com.scheduletrackapp.ui.kanban.KanbanScreen
import com.scheduletrackapp.ui.kanban.KanbanViewModel
import com.scheduletrackapp.ui.notification.NotificationDetailScreen
import com.scheduletrackapp.ui.notification.NotificationScreen
import com.scheduletrackapp.ui.theme.ScheduleTrackAppTheme
import java.util.concurrent.TimeUnit

enum class Screen {
    KANBAN,
    NOTIFICATION,
    NOTIFICATION_DETAIL
}

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()

        val lhpRepository = LhpRepositoryImpl(supabaseClient)
        val companyRepository = ClientCompanyRepositoryImpl(supabaseClient)

        val kanbanViewModel = KanbanViewModel(lhpRepository)
        val companyViewModel = ClientCompanyViewModel(companyRepository)

        val slaWorkRequest = PeriodicWorkRequestBuilder<SlaDeadlineWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "SlaDeadlineCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            slaWorkRequest
        )

        setContent {
            ScheduleTrackAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.KANBAN) }
                    var selectedNotification by remember { mutableStateOf<NotificationItem?>(null) }
                    val kanbanState by kanbanViewModel.uiState.collectAsState()

                    val dynamicNotifications = remember(kanbanState.records) {
                        kanbanState.records.filter {
                            it.deadlineStatus == DeadlineStatus.OVERDUE || it.deadlineStatus == DeadlineStatus.WARNING_NEAR_DEADLINE
                        }.map { record ->
                            NotificationItem(
                                id = record.id,
                                title = if (record.deadlineStatus == DeadlineStatus.OVERDUE) "🚨 SLA Deadline Terlampaui!" else "🟧 Peringatan SLA H-2",
                                message = "Berkas LHP #${record.lhpNumber} (${record.companyName} - ${record.unitDescription}) " +
                                        if (record.deadlineStatus == DeadlineStatus.OVERDUE) "telah melewati batas SLA!" else "mendekati deadline (${record.clientDeadline}).",
                                type = if (record.deadlineStatus == DeadlineStatus.OVERDUE) NotificationType.DEADLINE_OVERDUE else NotificationType.DEADLINE_WARNING,
                                timestamp = record.clientDeadline,
                                isRead = false,
                                lhpRecordId = record.lhpNumber
                            )
                        }
                    }

                    // Render Halaman Langsung Tanpa Animasi Halaman (Animasi Hanya Pada Floating BottomBar)
                    when (currentScreen) {
                        Screen.KANBAN -> {
                            KanbanScreen(
                                viewModel = kanbanViewModel,
                                companyViewModel = companyViewModel,
                                onNotificationClick = {
                                    currentScreen = Screen.NOTIFICATION
                                }
                            )
                        }

                        Screen.NOTIFICATION -> {
                            NotificationScreen(
                                notifications = dynamicNotifications,
                                onNotificationClick = { notification ->
                                    selectedNotification = notification
                                    currentScreen = Screen.NOTIFICATION_DETAIL
                                },
                                onBackClick = {
                                    currentScreen = Screen.KANBAN
                                },
                                onKanbanClick = {
                                    currentScreen = Screen.KANBAN
                                }
                            )
                        }

                        Screen.NOTIFICATION_DETAIL -> {
                            selectedNotification?.let { notification ->
                                NotificationDetailScreen(
                                    notification = notification,
                                    onBackClick = {
                                        currentScreen = Screen.NOTIFICATION
                                    },
                                    onNavigateToLhpBoard = { _ ->
                                        currentScreen = Screen.KANBAN
                                    }
                                )
                            } ?: run {
                                currentScreen = Screen.NOTIFICATION
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}