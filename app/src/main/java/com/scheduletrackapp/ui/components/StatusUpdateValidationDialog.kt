package com.scheduletrackapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.ui.kanban.PendingStatusUpdate
import com.scheduletrackapp.ui.theme.BgCanvasColor
import com.scheduletrackapp.ui.theme.CardWhite
import com.scheduletrackapp.ui.theme.PrimaryBlue
import com.scheduletrackapp.ui.theme.TextMain
import com.scheduletrackapp.ui.theme.TextMuted

@Composable
fun StatusUpdateValidationDialog(
    pending: PendingStatusUpdate,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Konfirmasi Perubahan Status",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    "Apakah anda yakin ingin merubah status berkas ini ke tahapan baru?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = BgCanvasColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            pending.record.lhpNumber,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Text(
                            pending.record.unitDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tahap Baru: ${pending.targetStatus.name.replace("_", " ")}",
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Konfirmasi Perubahan")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = CardWhite
    )
}