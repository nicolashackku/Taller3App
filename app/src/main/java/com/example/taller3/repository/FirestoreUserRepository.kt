package com.example.taller3.repository

import com.example.taller3.data.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class FirestoreUserRepository(
    db: FirebaseFirestore = Firebase.firestore
) : UserRepository {

    private val users = db.collection(USERS)

    override fun save(uid: String, info: UserInfo, onResult: (Result<Unit>) -> Unit) {
        users.document(uid).set(info.toMap(), SetOptions.merge()).addOnCompleteListener { onResult(it.toUnitResult()) }
    }

    override fun get(uid: String, onResult: (Result<UserInfo?>) -> Unit) {
        users.document(uid).get().addOnCompleteListener { task ->
            onResult(
                if (task.isSuccessful) Result.success(task.result?.toUserInfo())
                else Result.failure(task.exception ?: UnknownError())
            )
        }
    }

    override fun observe(uid: String, onChange: (Result<UserInfo?>) -> Unit): Subscription {
        val registration = users.document(uid).addSnapshotListener { snap, e ->
            onChange(if (e != null) Result.failure(e) else Result.success(snap?.toUserInfo()))
        }
        return Subscription { registration.remove() }
    }

    override fun observeOnline(onChange: (Result<Map<String, UserInfo>>) -> Unit): Subscription {
        val registration = users.whereEqualTo("online", true).addSnapshotListener { snap, e ->
            onChange(
                if (e != null) Result.failure(e)
                else Result.success(
                    snap?.documents.orEmpty()
                        .mapNotNull { doc -> doc.toUserInfo()?.let { doc.id to it } }
                        .toMap()
                )
            )
        }
        return Subscription { registration.remove() }
    }

    override fun update(uid: String, fields: Map<String, Any?>, onResult: (Result<Unit>) -> Unit) {
        users.document(uid).update(fields).addOnCompleteListener { onResult(it.toUnitResult()) }
    }

    private fun UserInfo.toMap() = mapOf(
        "displayName" to displayName,
        "photo" to photo,
        "phone" to phone,
        "online" to online,
        "latitude" to latitude,
        "longitude" to longitude,
    )

    private fun DocumentSnapshot.toUserInfo(): UserInfo? {
        if (!exists()) return null
        return UserInfo(
            displayName = getString("displayName") ?: return null,
            photo = getString("photo"),
            phone = getLong("phone") ?: 0L,
            online = getBoolean("online") ?: false,
            latitude = getDouble("latitude") ?: 0.0,
            longitude = getDouble("longitude") ?: 0.0,
        )
    }

    private companion object {
        const val USERS = "users"
    }
}

internal fun Task<*>.toUnitResult(): Result<Unit> =
    if (isSuccessful) Result.success(Unit) else Result.failure(exception ?: UnknownError())
