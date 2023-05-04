package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Favourites(
    val idUser: Int,
    val idTreasure: Int
)


