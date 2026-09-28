package com.scheduletrackapp.ui.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scheduletrackapp.domain.model.ClientCompany
import com.scheduletrackapp.domain.repository.ClientCompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientCompanyUiState(
    val isLoading: Boolean = false,
    val companies: List<ClientCompany> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ClientCompanyViewModel(
    private val repository: ClientCompanyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientCompanyUiState(isLoading = true))
    val uiState: StateFlow<ClientCompanyUiState> = _uiState.asStateFlow()

    init {
        loadCompanies()
    }

    fun loadCompanies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getClientCompanies()
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, companies = list) }
                }
        }
    }

    fun createCompany(name: String, picName: String, picPhone: String, address: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val newCompany = ClientCompany(
                companyName = name,
                picName = picName.ifBlank { "PIC Operasional" },
                picPhone = picPhone.ifBlank { "-" },
                address = address.ifBlank { "Alamat Pabrik Klien" }
            )
            repository.createClientCompany(newCompany)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Perusahaan berhasil disimpan ke Supabase!") }
                    loadCompanies()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun deleteCompany(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.deleteClientCompany(id)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Perusahaan berhasil dihapus!") }
                    loadCompanies()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Gagal menghapus! Perusahaan ini masih memiliki berkas LHP terikat."
                        )
                    }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}