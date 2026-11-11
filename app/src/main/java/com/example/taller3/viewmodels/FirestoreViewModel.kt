package com.example.taller3.viewmodels

import androidx.lifecycle.ViewModel
import com.example.taller3.R
import com.example.taller3.data.Coordinate
import com.example.taller3.data.UserInfo
import com.example.taller3.repository.FirestoreUserRepository
import com.example.taller3.repository.Subscription
import com.example.taller3.repository.UserRepository
import com.example.taller3.utils.UiText
import com.example.taller3.utils.orResource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class FirestoreState(
    val user: UserInfo? = null,
    val onlineUsers: Map<String, UserInfo> = emptyMap(),
    val routes: Map<String, List<Coordinate>> = emptyMap(),
    val loading: Boolean = false,
)

sealed interface FirestoreEvent {
    data class Error(val message: UiText) : FirestoreEvent
}

class FirestoreViewModel(
    private val userRepo: UserRepository = FirestoreUserRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(FirestoreState())
    val state: StateFlow<FirestoreState> = _state.asStateFlow()

    private val _events = Channel<FirestoreEvent>(Channel.BUFFERED)
    val events: Flow<FirestoreEvent> = _events.receiveAsFlow()

    private var userListener: Subscription? = null
    private var onlineListener: Subscription? = null

    fun saveUser(uid: String, info: UserInfo, onDone: (Boolean) -> Unit = {}) {
        _state.update { it.copy(loading = true) }
        userRepo.save(uid, info) { result ->
            _state.update { it.copy(loading = false) }
            result.onFailure(::emitError)
            onDone(result.isSuccess)
        }
    }

    fun observeUser(uid: String) {
        userListener?.cancel()
        userListener = userRepo.observe(uid) { result ->
            result.onSuccess { user -> _state.update { it.copy(user = user) } }
                .onFailure(::emitError)
        }
    }

    fun updateUser(uid: String, fields: Map<String, Any?>, onDone: (Boolean) -> Unit = {}) {
        userRepo.update(uid, fields) { result ->
            result.onFailure(::emitError)
            onDone(result.isSuccess)
        }
    }

    // Each position change of an online user is appended to its route; users that go offline lose it.
    fun observeOnlineUsers(selfUid: String) {
        onlineListener?.cancel()
        onlineListener = userRepo.observeOnline { result ->
            result.onSuccess { users ->
                val others = (users - selfUid).filterValues { it.latitude != 0.0 || it.longitude != 0.0 }
                _state.update { current ->
                    val routes = others.mapValues { (uid, user) ->
                        val route = current.routes[uid].orEmpty()
                        val point = Coordinate(user.latitude, user.longitude)
                        if (route.lastOrNull() == point) route else route + point
                    }
                    current.copy(onlineUsers = others, routes = routes)
                }
            }.onFailure(::emitError)
        }
    }

    fun stopObserving() {
        userListener?.cancel()
        userListener = null
        onlineListener?.cancel()
        onlineListener = null
        _state.update { FirestoreState() }
    }

    override fun onCleared() {
        userListener?.cancel()
        onlineListener?.cancel()
    }

    private fun emitError(e: Throwable) {
        _events.trySend(FirestoreEvent.Error(e.localizedMessage.orResource(R.string.firestore_view_model_unknown_error)))
    }
}
