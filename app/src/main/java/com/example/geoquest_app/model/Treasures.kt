package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Treasures(
    val idTreasure: Int,
    var name: String,
    var description: String,
    var image: String,
    var latitude: Double,
    var longitude: Double,
    var location: String,
    var clue: String,
    var status: String,
    var difficulty: String,
    var score: Double
)