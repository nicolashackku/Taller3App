/*
package com.example.taller3.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.taller3.R
import com.example.taller3.repository.FirebaseStorageRepository
import com.example.taller3.repository.StorageRepository
import com.example.taller3.utils.UiText
import com.example.taller3.utils.orResource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class StorageState(
    val uploading: Boolean = false,
)

sealed interface StorageEvent {
    data class Error(val message: UiText) : StorageEvent
}

class StorageViewModel(
    private val repo: StorageRepository = FirebaseStorageRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(StorageState())
    val state: StateFlow<StorageState> = _state.asStateFlow()

    private val _events = Channel<StorageEvent>(Channel.BUFFERED)
    val events: Flow<StorageEvent> = _events.receiveAsFlow()

    private var pendingUploads = 0

    // Stored at users/{uid}/{fileName}; onDone receives the download URL or null on failure.
    fun uploadUserFile(uid: String, fileName: String, uri: Uri, onDone: (String?) -> Unit = {}) {
        startUpload()
        repo.uploadUserFile(uid, fileName, uri) { finishUpload(it, onDone) }
    }

    // Stored at messages/{messageId}/{fileName}; onDone receives the download URL or null on failure.
    fun uploadMessageFile(
        messageId: String, fileName: String, uri: Uri, onDone: (String?) -> Unit = {},
    ) {
        startUpload()
        repo.uploadMessageFile(messageId, fileName, uri) { finishUpload(it, onDone) }
    }

    fun deleteUserFile(uid: String, fileName: String, onDone: (Boolean) -> Unit = {}) {
        repo.deleteUserFile(uid, fileName) { result ->
            result.onFailure(::emitError)
            onDone(result.isSuccess)
        }
    }

    fun deleteMessageFile(messageId: String, fileName: String, onDone: (Boolean) -> Unit = {}) {
        repo.deleteMessageFile(messageId, fileName) { result ->
            result.onFailure(::emitError)
            onDone(result.isSuccess)
        }
    }

    private fun startUpload() {
        pendingUploads++
        _state.update { it.copy(uploading = true) }
    }

    private fun finishUpload(result: Result<String>, onDone: (String?) -> Unit) {
        pendingUploads--
        _state.update { it.copy(uploading = pendingUploads > 0) }
        result.onFailure(::emitError)
        onDone(result.getOrNull())
    }

    private fun emitError(e: Throwable) {
        _events.trySend(StorageEvent.Error(e.localizedMessage.orResource(R.string.storage_view_model_unknown_error)))
    }
}
*/
