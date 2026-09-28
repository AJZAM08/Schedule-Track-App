package com.scheduletrackapp.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.Screen
import com.scheduletrackapp.domain.model.NotificationItem
import com.scheduletrackapp.domain.model.NotificationType
import com.scheduletrackapp.ui.components.FloatingBottomBar

private val BgCanvasColor = Color(0xFF141A23)
private val CardBgColor = Color(0xFF1E2634)
private val TextMain = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val FlagRed = Color(0xFFEF4444)
private val FlagOrange = Color(0xFFF97316)
private val PrimaryBlue = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notifications: List<NotificationItem>,
    onNotificationClick: (NotificationItem) -> Unit,
    onBackClick: () -> Unit,
    onKanbanClick: () -> Unit = onBackClick
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notifikasi Peringatan SLA", fontWeight = FontWeight.Bold, color = TextMain)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCanvasColor)
            )
        },
        bottomBar = {
            FloatingBottomBar(
                currentScreen = Screen.NOTIFICATION,
                onKanbanClick = onKanbanClick,
                onNotificationClick = {}
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgCanvasColor)
                .padding(innerPadding)
        ) {
            if (notifications.isEmpty()) {
                Text(
                    text = "Belum Ada Notifikasi Peringatan SLA",
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { item ->
                        NotificationCardItem(
                            item = item,
                            onClick = { onNotificationClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCardItem(
    item: NotificationItem,
    onClick: () -> Unit
) {
    val (iconBgColor, iconEmoji) = when (item.type) {
        NotificationType.DEADLINE_OVERDUE -> FlagRed.copy(alpha = 0.2f) to "🚨"
        NotificationType.DEADLINE_WARNING -> FlagOrange.copy(alpha = 0.2f) to "🟧"
        NotificationType.STATUS_CHANGED -> PrimaryBlue.copy(alpha = 0.2f) to "🔵"
        NotificationType.SYSTEM_INFO -> Color.Gray.copy(alpha = 0.2f) to "ℹ️"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlue
                )
            }
        }
    }
}