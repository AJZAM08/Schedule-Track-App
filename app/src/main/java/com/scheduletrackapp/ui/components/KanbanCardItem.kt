package com.scheduletrackapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.domain.model.DeadlineStatus
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus

private val CardWhite = Color(0xFFFFFFFF)
private val TextMain = Color(0xFF1E293B)
private val TextMuted = Color(0xFF94A3B8)
private val JiraBlue = Color(0xFF2563EB)
private val FlagRed = Color(0xFFEF4444)
private val FlagOrange = Color(0xFFF97316)
private val FlagGreen = Color(0xFF10B981)

@Composable
fun KanbanCardItem(
    record: LhpRecord,
    isBeingDragged: Boolean = false,
    onStartDrag: (Offset) -> Unit = {},
    onDrag: (Offset) -> Unit = {},
    onStopDrag: () -> Unit = {},
    onInitiateStatusUpdate: (LhpRecord, LhpStatus) -> Unit = { _, _ -> }
) {
    val (flagEmoji, flagColor) = when (record.deadlineStatus) {
        DeadlineStatus.OVERDUE -> "🚩" to FlagRed
        DeadlineStatus.WARNING_NEAR_DEADLINE -> "🟧" to FlagOrange
        DeadlineStatus.SAFE -> "🟢" to FlagGreen
    }

    val nextStatus = when (record.currentStatus) {
        LhpStatus.SCHEDULED -> LhpStatus.INSPECTION_COMPLETED
        LhpStatus.INSPECTION_COMPLETED -> LhpStatus.DRAFT_CREATED
        LhpStatus.DRAFT_CREATED -> LhpStatus.AHLI_K3_REVIEW
        LhpStatus.AHLI_K3_REVIEW -> LhpStatus.DISNAKER_PROCESS
        LhpStatus.DISNAKER_PROCESS -> LhpStatus.CERTIFICATE_ISSUED
        LhpStatus.CERTIFICATE_ISSUED -> LhpStatus.DELIVERED_TO_CLIENT
        LhpStatus.DELIVERED_TO_CLIENT -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isBeingDragged) 0.3f else 1.0f)
            .pointerInput(record.id) {
                // 👈 Menggunakan Long Press agar scrolling tetap lancar saat data banyak
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset -> onStartDrag(offset) },
                    onDragEnd = { onStopDrag() },
                    onDragCancel = { onStopDrag() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    }
                )
            }
            .clickable {
                if (nextStatus != null) {
                    onInitiateStatusUpdate(record, nextStatus)
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = record.unitDescription,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = flagEmoji, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = record.companyName.ifEmpty { "Klien ID: ${record.clientCompanyId}" },
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = flagColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Deadline: ${record.clientDeadline}",
                    style = MaterialTheme.typography.labelMedium,
                    color = flagColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }