package com.example.geoquest_app.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) {
        multiplier *= 10
    }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}