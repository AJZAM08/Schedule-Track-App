package com.scheduletrackapp.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scheduletrackapp.domain.model.ClientCompany
import com.scheduletrackapp.ui.company.ClientCompanyViewModel

private val BgCanvasColor = Color(0xFF141A23)
private val CardBgColor = Color(0xFF1E2634)
private val TextMain = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val JiraBlue = Color(0xFF2563EB)
private val FlagRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCompanyManagerBottomSheet(
    viewModel: ClientCompanyViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var showAddForm by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("") }
    var picName by remember { mutableStateOf("") }
    var picPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = BgCanvasColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = JiraBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kelola Klien & Perusahaan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                }

                IconButton(onClick = { showAddForm = !showAddForm }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Perusahaan",
                        tint = JiraBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Form Input Perusahaan Baru
            if (showAddForm) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBgColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tambah Detail Perusahaan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Nama Perusahaan") },
                            placeholder = { Text("Contoh: PT Semen Indonesia") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = picName,
                            onValueChange = { picName = it },
                            label = { Text("Nama PIC K3") },
                            placeholder = { Text("Contoh: Budi Santoso") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = picPhone,
                            onValueChange = { picPhone = it },
                            label = { Text("No. Telepon PIC") },
                            placeholder = { Text("Contoh: 08123456789") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Alamat Perusahaan") },
                            placeholder = { Text("Contoh: Jl. Industri No. 45, Jakarta") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (companyName.isNotBlank()) {
                                    viewModel.createCompany(companyName, picName, picPhone, address)
                                    companyName = ""
                                    picName = ""
                                    picPhone = ""
                                    address = ""
                                    showAddForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = JiraBlue)
                        ) {
                            Text("Simpan Ke Database", color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(24.dp),
                    color = JiraBlue
                )
            } else if (state.companies.isEmpty()) {
                Text(
                    text = "Belum ada perusahaan terdaftar di Supabase.",
                    color = TextMuted,
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxHeight(0.7f)
                ) {
                    items(state.companies) { company ->
                        CompanyCardItem(
                            company = company,
                            onDeleteClick = { viewModel.deleteCompany(company.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyCardItem(
    company: ClientCompany,
    onDeleteClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBgColor),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = company.companyName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "👤 PIC: ${company.picName} (${company.picPhone})",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                if (!company.address.isNullOrBlank()) {
                    Text(
                        text = "📍 ${company.address}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = FlagRed
                )
            }
        }
    }
}