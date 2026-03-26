package com.example.picturepuzzle.data.firebase

import com.example.picturepuzzle.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun saveProfile(profile: UserProfile) {
        db.collection("users")
            .document(profile.uid)
            .set(profile)
            .await()
    }

    suspend fun getProfile(uid: String): UserProfile? {
        val doc = db.collection("users")
            .document(uid)
            .get()
            .await()
        return doc.toObject(UserProfile::class.java)
    }
}