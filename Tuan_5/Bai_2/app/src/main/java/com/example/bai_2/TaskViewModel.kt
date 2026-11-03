package com.example.bai_2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TaskViewModel(private val repo: TaskRepository = TaskRepository()) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TodoTask>>(emptyList())
    val tasks: StateFlow<List<TodoTask>> = _tasks

    private val _welcome = MutableStateFlow("")
    val welcome: StateFlow<String> = _welcome

    private val _errors = MutableSharedFlow<String>()
    val errors: SharedFlow<String> = _errors

    private var job: Job? = null

    init {
        viewModelScope.launch { _welcome.value = RemoteConfigHelper.load() }
    }

    // Gọi sau khi đăng nhập: lắng nghe Firestore theo uid
    fun start() {
        job?.cancel()
        job = viewModelScope.launch {
            repo.observeTasks()
                .catch { _errors.emit("Lỗi Firestore: ${it.localizedMessage}") }
                .collect { _tasks.value = it }
        }
    }

    // Gọi khi đăng xuất
    fun stop() {
        job?.cancel()
        _tasks.value = emptyList()
    }

    fun add(title: String, tag: String, time: String) = viewModelScope.launch {
        try { repo.add(title, tag, time) }
        catch (e: Exception) { _errors.emit("Không thêm được việc: ${e.localizedMessage}") }
    }

    fun toggle(t: TodoTask) = viewModelScope.launch {
        try { repo.setDone(t.id, !t.done) }
        catch (e: Exception) { _errors.emit("Không cập nhật được: ${e.localizedMessage}") }
    }

    fun delete(id: String) = viewModelScope.launch {
        try { repo.delete(id) }
        catch (e: Exception) { _errors.emit("Không xóa được: ${e.localizedMessage}") }
    }
}