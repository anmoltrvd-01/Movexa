package com.movexa.android.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String? = null,
    val age: Int? = null,
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val goal: String? = null
)