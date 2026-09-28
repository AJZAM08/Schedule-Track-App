package com.scheduletrackapp.data.repository

import com.scheduletrackapp.data.remote.dto.ClientCompanyDto
import com.scheduletrackapp.data.remote.dto.LhpDto
import com.scheduletrackapp.data.remote.dto.toDomain
import com.scheduletrackapp.data.remote.dto.toDto
import com.scheduletrackapp.domain.model.LhpRecord
import com.scheduletrackapp.domain.model.LhpStatus
import com.scheduletrackapp.domain.repository.LhpRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LhpRepositoryImpl(
    private val supabase: SupabaseClient
) : LhpRepository {

    override fun getLhpRecords(): Flow<List<LhpRecord>> = flow {
        // Ambil data langsung dari Supabase PostgREST Database
        val companies = supabase.from("client_companies").select().decodeList<ClientCompanyDto>()
        val companyMap = companies.associate { (it.id ?: "") to it.companyName }

        val lhpList = supabase.from("lhp_records").select().decodeList<LhpDto>()
        emit(lhpList.map { it.toDomain(companyMap) })
    }.catch { error ->
        error.printStackTrace()
        emit(emptyList())
    }

    override fun getLhpById(id: String): Flow<LhpRecord?> {
        return getLhpRecords().map { list -> list.find { it.id == id } }
    }

    override suspend fun createLhpRecord(record: LhpRecord): Result<Unit> = runCatching {
        val companyDto = ClientCompanyDto(
            companyName = record.companyName.ifBlank { "PT Klien K3" },
            picName = "PIC Operasional",
            picPhone = "08123456789",
            address = "Alamat Pabrik Klien"
        )

        val insertedCompany = supabase.from("client_companies")
            .insert(companyDto) {
                select()
            }
            .decodeSingle<ClientCompanyDto>()

        val lhpDto = record.toDto().copy(
            clientCompanyId = insertedCompany.id!!
        )

        supabase.from("lhp_records").insert(lhpDto)
    }

    override suspend fun updateLhpStatus(
        id: String,
        newStatus: LhpStatus,
        notes: String?
    ): Result<Unit> = runCatching {
        supabase.from("lhp_records").update(
            mapOf("current_status" to newStatus.name)
        ) {
            filter { eq("id", id) }
        }
    }

    override suspend fun deleteLhpRecord(id: String): Result<Unit> = runCatching {
        supabase.from("lhp_records").delete {
            filter { eq("id", id) }
        }
    }
}