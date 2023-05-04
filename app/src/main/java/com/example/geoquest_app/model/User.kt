package com.example.models

import kotlinx.serialization.Serializable


@Serializable
data class User(
    var idUser: Int,
    var nickName: String,
    var email: String,
    var password: String,
    var photo: String,
    var userLevel: String,
    var userRole: String,
    var favs: List<Treasures>
)

