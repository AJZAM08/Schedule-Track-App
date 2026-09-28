package com.scheduletrackapp.ui.kanban

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus
import com.scheduletrackapp.ui.company.ClientCompanyViewModel
import com.scheduletrackapp.ui.components.ClientCompanyManagerBottomSheet
import com.scheduletrackapp.ui.components.FloatingBottomBar
import com.scheduletrackapp.ui.components.KanbanStatusSection
import com.scheduletrackapp.ui.components.StatusUpdateValidationDialog
import com.scheduletrackapp.ui.components.CreateLhpBottomSheet
import com.scheduletrackapp.ui.components.KanbanCardItem
import kotlin.math.roundToInt

private val BgCanvasColor = Color(0xFF141A23)
private val TextMain = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val JiraBlue = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    viewModel: KanbanViewModel,
    companyViewModel: ClientCompanyViewModel,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    var showCreateBottomSheet by remember { mutableStateOf(false) }
    var showCompanyManagerBottomSheet by remember { mutableStateOf(false) }

    var draggingRecord by remember { mutableStateOf<LhpRecord?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    val companyState by companyViewModel.uiState.collectAsState()

    val showUnderDevToast = {
        Toast.makeText(context, "Fitur sedang dalam pengembangan 🚀", Toast.LENGTH_SHORT).show()
    }

    // Pop-up Validasi Konfirmasi
    state.pendingStatusUpdate?.let { pending ->
        StatusUpdateValidationDialog(
            pending = pending,
            onConfirm = {
                viewModel.onConfirmStatusUpdate()
                draggingRecord = null
            },
            onDismiss = {
                viewModel.onCancelStatusUpdate()
                draggingRecord = null
            }
        )
    }

    if (showCreateBottomSheet) {
        CreateLhpBottomSheet(
            registeredCompanies = companyState.companies,
            onDismiss = { showCreateBottomSheet = false },
            onSubmit = { lhpNum, company, cat, unit, inspectDate, deadline ->
                viewModel.createLhpRecord(lhpNum, company, cat, unit, inspectDate, deadline)
            },
            onNavigateToAddCompany = {
                showCompanyManagerBottomSheet = true
            }
        )
    }

    if (showCompanyManagerBottomSheet) {
        ClientCompanyManagerBottomSheet(
            viewModel = companyViewModel,
            onDismiss = { showCompanyManagerBottomSheet = false }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCanvasColor)
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ScheduleTrack / Board",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showUnderDevToast() }
                    ) {
                        Text(
                            text = "Status LHP Board",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "˅", fontSize = 16.sp, color = TextMain, fontWeight = FontWeight.Bold)
                    }
                }

                // Tombol Kelola Perusahaan Klien (🏢)
                IconButton(
                    onClick = { showCompanyManagerBottomSheet = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Kelola Klien",
                        tint = TextMain
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateBottomSheet = true },
                containerColor = JiraBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        },
        bottomBar = {
            FloatingBottomBar(
                onNotificationClick = onNotificationClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BgCanvasColor)
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = JiraBlue
                )
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "Terjadi kesalahan",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val statuses = LhpStatus.values()
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(statuses) { status ->
                        val recordsInStatus = state.records.filter { it.currentStatus == status }

                        val nextAllowedStatus = draggingRecord?.let { record ->
                            when (record.currentStatus) {
                                LhpStatus.SCHEDULED -> LhpStatus.INSPECTION_COMPLETED
                                LhpStatus.INSPECTION_COMPLETED -> LhpStatus.DRAFT_CREATED
                                LhpStatus.DRAFT_CREATED -> LhpStatus.AHLI_K3_REVIEW
                                LhpStatus.AHLI_K3_REVIEW -> LhpStatus.DISNAKER_PROCESS
                                LhpStatus.DISNAKER_PROCESS -> LhpStatus.CERTIFICATE_ISSUED
                                LhpStatus.CERTIFICATE_ISSUED -> LhpStatus.DELIVERED_TO_CLIENT
                                LhpStatus.DELIVERED_TO_CLIENT -> null
                            }
                        }
                        val isTargetColumn = status == nextAllowedStatus

                        KanbanStatusSection(
                            status = status,
                            records = recordsInStatus,
                            draggingRecord = draggingRecord,
                            isTargetColumn = isTargetColumn,
                            onStartDrag = { record, offset ->
                                draggingRecord = record
                                dragPosition = offset
                            },
                            onDrag = { changeOffset ->
                                dragPosition += changeOffset
                            },
                            onStopDrag = {
                                draggingRecord = null
                            },
                            onDropInTargetStatus = { targetStatus ->
                                draggingRecord?.let { record ->
                                    viewModel.onInitiateStatusUpdate(record, targetStatus)
                                }
                            }
                        )
                    }
                }
            }

            draggingRecord?.let { record ->
                Box(
                    modifier = Modifier
                        .offset { IntOffset(dragPosition.x.roundToInt(), dragPosition.y.roundToInt()) }
                        .graphicsLayer {
                            scaleX = 1.05f
                            scaleY = 1.05f
                            shadowElevation = 16f
                        }
                ) {
                    KanbanCardItem(
                        record = record,
                        isBeingDragged = true
                    )
                }
            }
        }
    }
}