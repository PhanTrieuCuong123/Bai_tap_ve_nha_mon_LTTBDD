package com.example.bai_2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Lưu công việc tại: users/{uid}/tasks/{taskId} */
class TaskRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun col(): CollectionReference {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Chưa đăng nhập")
        return db.collection("users").document(uid).collection("tasks")
    }

    fun observeTasks(): Flow<List<TodoTask>> = callbackFlow {
        val reg = col().orderBy("createdAt").addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents?.map { d ->
                TodoTask(
                    id = d.id,
                    title = d.getString("title") ?: "",
                    tag = d.getString("tag") ?: "Học tập",
                    time = d.getString("time") ?: "",
                    done = d.getBoolean("done") ?: false
                )
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    suspend fun add(title: String, tag: String, time: String) {
        col().add(
            hashMapOf(
                "title" to title,
                "tag" to tag,
                "time" to time,
                "done" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun setDone(id: String, done: Boolean) {
        col().document(id).update("done", done).await()
    }

    suspend fun delete(id: String) {
        col().document(id).delete().await()
    }
}
