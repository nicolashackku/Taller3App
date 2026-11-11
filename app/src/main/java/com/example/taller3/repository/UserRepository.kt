package com.example.taller3.repository

import com.example.taller3.data.UserInfo

fun interface Subscription {
    fun cancel()
}

interface UserRepository {
    fun save(uid: String, info: UserInfo, onResult: (Result<Unit>) -> Unit)
    fun get(uid: String, onResult: (Result<UserInfo?>) -> Unit)
    fun observe(uid: String, onChange: (Result<UserInfo?>) -> Unit): Subscription
    fun observeOnline(onChange: (Result<Map<String, UserInfo>>) -> Unit): Subscription
    fun update(uid: String, fields: Map<String, Any?>, onResult: (Result<Unit>) -> Unit)
}
