package com.example.bai_2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Realtime Database: ghi chú nhanh lưu tại notes/{uid} */
class NoteRepository {
    private fun ref(): DatabaseReference {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Chưa đăng nhập")
        return FirebaseDatabase.getInstance().getReference("notes").child(uid)
    }

    fun observe(): Flow<String> = callbackFlow {
        val r = ref()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(String::class.java) ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        r.addValueEventListener(listener)
        awaitClose { r.removeEventListener(listener) }
    }

    suspend fun save(text: String) {
        ref().setValue(text).await()
    }
}
