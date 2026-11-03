package com.example.bai_2

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Gom các tính năng ở màn Hồ sơ: Storage (ảnh), Realtime Database (ghi chú), FCM (token). */
class ProfileViewModel(private val notes: NoteRepository = NoteRepository()) : ViewModel() {

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token

    private val _uploading = MutableStateFlow(false)
    val uploading: StateFlow<Boolean> = _uploading

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    private var noteJob: Job? = null

    /** Gọi mỗi khi trạng thái đăng nhập đổi. */
    fun onUserChanged(loggedIn: Boolean) {
        noteJob?.cancel()
        if (!loggedIn) {
            _photoUrl.value = null
            _note.value = ""
            _token.value = ""
            return
        }
        _photoUrl.value = FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()
        noteJob = viewModelScope.launch {
            notes.observe()
                .catch { _messages.emit("Lỗi Realtime Database: ${it.localizedMessage}") }
                .collect { _note.value = it }
        }
    }

    fun saveNote(text: String) = viewModelScope.launch {
        try {
            notes.save(text)
            _messages.emit("Đã lưu ghi chú")
        } catch (e: Exception) {
            _messages.emit("Không lưu được ghi chú: ${e.localizedMessage}")
        }
    }

    // ---- Storage ----
    fun uploadAvatar(uri: Uri) = viewModelScope.launch {
        _uploading.value = true
        try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException("Chưa đăng nhập")
            val ref = FirebaseStorage.getInstance().reference.child("avatars/${user.uid}.jpg")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await()
            user.updateProfile(userProfileChangeRequest { photoUri = url }).await()
            // thêm tham số t để Coil không dùng ảnh cũ trong cache
            _photoUrl.value = "$url&t=${System.currentTimeMillis()}"
            _messages.emit("Đã cập nhật ảnh đại diện")
        } catch (e: Exception) {
            _messages.emit("Upload ảnh lỗi: ${e.localizedMessage}")
        } finally {
            _uploading.value = false
        }
    }

    // ---- Cloud Messaging ----
    fun loadToken() = viewModelScope.launch {
        try {
            _token.value = FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            _messages.emit("Không lấy được FCM token: ${e.localizedMessage}")
        }
    }
}
