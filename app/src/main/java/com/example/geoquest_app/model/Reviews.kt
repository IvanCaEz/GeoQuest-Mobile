package com.example.geoquest_app.model

import kotlinx.serialization.Serializable

@Serializable
data class Reviews (
    val idReview: Int,
    val idTreasure: Int,
    val idUser: Int,
    var opinion: String,
    var rating: Int,
    var photo: String
)
