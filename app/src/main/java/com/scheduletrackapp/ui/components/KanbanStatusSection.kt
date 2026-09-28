package com.scheduletrackapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus

private val ColumnBgColor = Color(0xFF1E2634)
private val JiraBlue = Color(0xFF2563EB)
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun KanbanStatusSection(
    status: LhpStatus,
    records: List<LhpRecord>,
    draggingRecord: LhpRecord?,
    isTargetColumn: Boolean, // 👈 2. Parameter isTargetColumn
    onStartDrag: (LhpRecord, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onStopDrag: () -> Unit,
    onDropInTargetStatus: (LhpStatus) -> Unit
) {
    // 👈 3. Penanganan Exhaustive Status termasuk DELIVERED_TO_CLIENT
    val (statusIcon, statusTitle) = when (status) {
        LhpStatus.SCHEDULED -> "📦" to "Jadwal Lapangan"
        LhpStatus.INSPECTION_COMPLETED -> "🛠️" to "Pemeriksaan Selesai"
        LhpStatus.DRAFT_CREATED -> "📝" to "Draft LHP Dibuat"
        LhpStatus.AHLI_K3_REVIEW -> "👁️" to "Review Ahli K3"
        LhpStatus.DISNAKER_PROCESS -> "🏛️" to "Proses Disnaker"
        LhpStatus.CERTIFICATE_ISSUED -> "✅" to "Sertifikat Terbit"
        LhpStatus.DELIVERED_TO_CLIENT -> "🏁" to "Diserahkan ke Klien"
    }

    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isTargetColumn) ColumnBgColor.copy(alpha = 0.9f) else ColumnBgColor)
            .border(
                width = if (isTargetColumn) 2.dp else 0.dp,
                color = if (isTargetColumn) JiraBlue else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                if (isTargetColumn) {
                    onDropInTargetStatus(status)
                }
            }
            .padding(14.dp)
    ) {
        if (isTargetColumn) {
            Surface(
                color = JiraBlue,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDropInTargetStatus(status) }
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Transition to...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = statusTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFF334155),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${records.size}",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (records.isEmpty() && !isTargetColumn) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum Ada Berkas",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(records) { record ->
                    val isBeingDragged = draggingRecord?.id == record.id

                    KanbanCardItem(
                        record = record,
                        isBeingDragged = isBeingDragged,
                        onStartDrag = { initialOffset -> onStartDrag(record, initialOffset) },
                        onDrag = onDrag,
                        onStopDrag = onStopDrag,
                        onInitiateStatusUpdate = { rec, nextStat ->
                            onDropInTargetStatus(nextStat)
                        }
                    )
                }

                if (isTargetColumn) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, JiraBlue, RoundedCornerShape(14.dp))
                                .background(JiraBlue.copy(alpha = 0.15f))
                                .clickable { onDropInTargetStatus(status) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+ Pindahkan Berkas Ke Sini",
                                color = JiraBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}