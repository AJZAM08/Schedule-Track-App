package com.scheduletrackapp.domain.repository

import com.scheduletrackapp.domain.model.ClientCompany
import kotlinx.coroutines.flow.Flow

interface ClientCompanyRepository {
    fun getClientCompanies(): Flow<List<ClientCompany>>
    suspend fun createClientCompany(company: ClientCompany): Result<Unit>
    suspend fun updateClientCompany(company: ClientCompany): Result<Unit>
    suspend fun deleteClientCompany(id: String): Result<Unit>
}