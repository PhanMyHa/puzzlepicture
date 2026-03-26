package com.example.picturepuzzle.data.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun ensureSignedIn(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) return currentUser.uid

        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw IllegalStateException("Anonymous auth failed")
    }

    fun getUid(): String? = auth.currentUser?.uid
}