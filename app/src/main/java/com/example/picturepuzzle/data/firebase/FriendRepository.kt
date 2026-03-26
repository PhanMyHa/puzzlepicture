package com.example.picturepuzzle.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Friend(
    val uid: String = "",
    val nickname: String = "Player"
)

class FriendRepository(
    private val authManager: AuthManager
) {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addFriend(friendUid: String) {
        val myUid = authManager.ensureSignedIn()
        val data = mapOf("uid" to friendUid)
        db.collection("users")
            .document(myUid)
            .collection("friends")
            .document(friendUid)
            .set(data)
            .await()
    }

    suspend fun getFriends(): List<Friend> {
        val myUid = authManager.ensureSignedIn()
        val snapshot = db.collection("users")
            .document(myUid)
            .collection("friends")
            .get()
            .await()

        return snapshot.documents.map {
            Friend(uid = it.getString("uid") ?: "")
        }
    }
}