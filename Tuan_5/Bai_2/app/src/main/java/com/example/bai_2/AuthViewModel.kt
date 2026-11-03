package com.example.bai_2

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthEvent {
    data class Success(val message: String) : AuthEvent
    data class Error(val message: String) : AuthEvent
}

class AuthViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(repo.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events

    fun signInWithGoogle(context: Context, webClientId: String) = launchAuth {
        val token = repo.getGoogleIdToken(context, webClientId)
        repo.signInWithGoogle(token)
    }

    fun signInAnonymously() = launchAuth { repo.signInAnonymously() }

    fun signOut() {
        repo.signOut()
        _user.value = null
    }

    private fun launchAuth(block: suspend () -> FirebaseUser) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _user.value = block()
                _events.emit(AuthEvent.Success("Đăng nhập thành công!"))
            } catch (e: Exception) {
                _events.emit(AuthEvent.Error("Đăng nhập lỗi: ${e.localizedMessage ?: "Không xác định"}"))
            } finally {
                _loading.value = false
            }
        }
    }
}