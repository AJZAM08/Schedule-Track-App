package com.scheduletrackapp.data.remote.dto

import com.scheduletrackapp.domain.model.ClientCompany
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientCompanyDto(
    val id: String? = null,
    @SerialName("company_name")
    val companyName: String,
    @SerialName("pic_name")
    val picName: String,
    @SerialName("pic_phone")
    val picPhone: String,
    val address: String? = null
)
// Mapper DTO Supabase -> Domain Model
fun ClientCompanyDto.toDomain(): ClientCompany {
    return ClientCompany(
        id = id.orEmpty(),
        companyName = companyName,
        picName = picName,
        picPhone = picPhone,
        address = address
    )
}

// Mapper Domain Model -> DTO Supabase
fun ClientCompany.toDto(): ClientCompanyDto {
    return ClientCompanyDto(
        id = id.ifEmpty { null },
        companyName = companyName,
        picName = picName,
        picPhone = picPhone,
        address = address,
    )
}