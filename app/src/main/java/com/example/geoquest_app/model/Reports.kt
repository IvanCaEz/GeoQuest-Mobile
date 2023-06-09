package com.example.geoquest_app.model

import kotlinx.serialization.Serializable

@Serializable
data class Reports(
    val idReport: Int,
    val idUser: Int,
    val idTreasure: Int,
    val reportInfo: String,
    val reportDate: String
)
