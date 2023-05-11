package com.example.geoquest_app.model.auth

import com.example.models.User

data class TokenResponse(val token: String, val user: User)