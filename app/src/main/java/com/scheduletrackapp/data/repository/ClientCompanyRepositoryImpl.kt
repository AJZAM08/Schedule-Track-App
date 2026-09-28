package com.scheduletrackapp.data.repository

import com.scheduletrackapp.data.remote.dto.ClientCompanyDto
import com.scheduletrackapp.data.remote.dto.toDomain
import com.scheduletrackapp.data.remote.dto.toDto
import com.scheduletrackapp.domain.model.ClientCompany
import com.scheduletrackapp.domain.repository.ClientCompanyRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ClientCompanyRepositoryImpl(
    private val supabase: SupabaseClient
) : ClientCompanyRepository {

    override fun getClientCompanies(): Flow<List<ClientCompany>> = flow {
        val dtoList = supabase.from("client_companies").select().decodeList<ClientCompanyDto>()
        emit(dtoList.map { it.toDomain() })
    }.catch { error ->
        error.printStackTrace()
        emit(emptyList())
    }

    override suspend fun createClientCompany(company: ClientCompany): Result<Unit> = runCatching {
        supabase.from("client_companies").insert(company.toDto())
    }

    override suspend fun updateClientCompany(company: ClientCompany): Result<Unit> = runCatching {
        supabase.from("client_companies").update(company.toDto()) {
            filter { eq("id", company.id) }
        }
    }

    override suspend fun deleteClientCompany(id: String): Result<Unit> = runCatching {
        supabase.from("client_companies").delete {
            filter { eq("id", id) }
        }
    }
}