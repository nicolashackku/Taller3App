/*
package com.example.taller3.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class FirebaseStorageRepository(
    private val storage: FirebaseStorage = Firebase.storage,
) : StorageRepository {

    override fun uploadUserFile(
        uid: String, fileName: String, uri: Uri, onResult: (Result<String>) -> Unit,
    ) = upload(userRef(uid, fileName), uri, onResult)

    override fun uploadMessageFile(
        messageId: String, fileName: String, uri: Uri, onResult: (Result<String>) -> Unit,
    ) = upload(messageRef(messageId, fileName), uri, onResult)

    override fun deleteUserFile(uid: String, fileName: String, onResult: (Result<Unit>) -> Unit) {
        userRef(uid, fileName).delete().addOnCompleteListener { onResult(it.toUnitResult()) }
    }

    override fun deleteMessageFile(
        messageId: String, fileName: String, onResult: (Result<Unit>) -> Unit,
    ) {
        messageRef(messageId, fileName).delete().addOnCompleteListener { onResult(it.toUnitResult()) }
    }

    private fun userRef(uid: String, fileName: String) =
        storage.reference.child("$USERS/$uid/$fileName")

    private fun messageRef(messageId: String, fileName: String) =
        storage.reference.child("$MESSAGES/$messageId/$fileName")

    // Uploads the file and resolves to its download URL.
    private fun upload(ref: StorageReference, uri: Uri, onResult: (Result<String>) -> Unit) {
        ref.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception ?: UnknownError()
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            onResult(
                if (task.isSuccessful) Result.success(task.result.toString())
                else Result.failure(task.exception ?: UnknownError())
            )
        }
    }

    private companion object {
        const val USERS = "users"
        const val MESSAGES = "messages"
    }
}
*/
