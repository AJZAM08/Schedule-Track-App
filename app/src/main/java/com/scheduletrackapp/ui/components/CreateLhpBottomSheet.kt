package com.scheduletrackapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.domain.model.ClientCompany
import com.scheduletrackapp.domain.model.K3Category
import com.scheduletrackapp.ui.theme.CardWhite
import com.scheduletrackapp.ui.theme.PrimaryBlue
import com.scheduletrackapp.ui.theme.TextMain
import com.scheduletrackapp.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLhpBottomSheet(
    registeredCompanies: List<ClientCompany>,
    onDismiss: () -> Unit,
    onSubmit: (lhpNumber: String, companyName: String, category: K3Category, unitDescription: String, inspectionDate: String, clientDeadline: String) -> Unit,
    onNavigateToAddCompany: () -> Unit = {}
) {
    var lhpNumber by remember { mutableStateOf("LHP/${System.currentTimeMillis().toString().takeLast(6)}") }
    var selectedCompany by remember { mutableStateOf<ClientCompany?>(registeredCompanies.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var unitDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(K3Category.PESAWAT_ANGKAT_ANGKUT) }
    var inspectionDate by remember { mutableStateOf("2026-09-25") }
    var clientDeadline by remember { mutableStateOf("2026-10-05") }

    LaunchedEffect(registeredCompanies) {
        if (selectedCompany == null && registeredCompanies.isNotEmpty()) {
            selectedCompany = registeredCompanies.first()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = CardWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Buat Berkas LHP Baru",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )

            Spacer(modifier = Modifier.height(16.dp))

            // No. LHP Auto-generated / Editable
            OutlinedTextField(
                value = lhpNumber,
                onValueChange = { lhpNumber = it },
                label = { Text("No. Registrasi LHP *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🏢 DROPDOWN MANDATORY PERUSAHAAN KLIEN
            Text(
                text = "Perusahaan Klien * (Wajib Pilihan Terdaftar)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (registeredCompanies.isEmpty()) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️ Belum ada perusahaan terdaftar!",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626)
                        )
                        TextButton(onClick = {
                            onDismiss()
                            onNavigateToAddCompany()
                        }) {
                            Text("+ Kelola Perusahaan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCompany?.companyName ?: "Pilih Perusahaan Klien",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        registeredCompanies.forEach { company ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(company.companyName, fontWeight = FontWeight.Bold)
                                        Text("PIC: ${company.picName}", fontSize = 11.sp, color = TextMuted)
                                    }
                                },
                                onClick = {
                                    selectedCompany = company
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Deskripsi Unit Alat K3
            OutlinedTextField(
                value = unitDescription,
                onValueChange = { unitDescription = it },
                label = { Text("Deskripsi Unit Alat / Instalasi K3 *") },
                placeholder = { Text("Contoh: Overhead Crane 15 Ton") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Kategori K3:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

            // Selector Kategori K3
            ScrollableTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                K3Category.entries.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category.name.replace("_", " "), fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = inspectionDate,
                    onValueChange = { inspectionDate = it },
                    label = { Text("Tanggal Riksa") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = clientDeadline,
                    onValueChange = { clientDeadline = it },
                    label = { Text("Deadline Klien") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isFormValid = selectedCompany != null && unitDescription.isNotBlank()

            // Tombol Simpan
            Button(
                onClick = {
                    val company = selectedCompany
                    if (company != null && unitDescription.isNotBlank()) {
                        onSubmit(lhpNumber, company.companyName, selectedCategory, unitDescription, inspectionDate, clientDeadline)
                        onDismiss()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = if (isFormValid) "Simpan & Tambahkan ke Kanban" else "Pilih Perusahaan & Isi Deskripsi",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}