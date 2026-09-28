package com.scheduletrackapp.domain.model

data class ClientCompany(
    val id: String = "",
    val companyName: String,
    val picName: String,
    val picPhone: String,
    val address: String? = null
)