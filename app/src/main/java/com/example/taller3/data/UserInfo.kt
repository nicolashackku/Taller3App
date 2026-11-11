package com.example.taller3.data

data class UserInfo(
    val displayName: String = "",
    val photo: String? = null,
    val phone: Long = 0L,
    val online: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
