package com.example.picturepuzzle.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun login(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun loginAnonymous(): FirebaseUser? {
        val result = auth.signInAnonymously().await()
        return result.user
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUser(): FirebaseUser? = auth.currentUser
}