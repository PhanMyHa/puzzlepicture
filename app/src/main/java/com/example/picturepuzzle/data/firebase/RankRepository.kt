package com.example.picturepuzzle.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class RankEntry(
    val uid: String = "",
    val imageId: Int = 0,
    val gridSize: Int = 3,
    val bestTime: Long = 0,
    val bestMoves: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

class RankRepository(
    private val authManager: AuthManager
) {
    private val db = FirebaseFirestore.getInstance()

    suspend fun submitScore(imageId: Int, gridSize: Int, time: Long, moves: Int) {
        val uid = authManager.ensureSignedIn()

        val docId = "${uid}_${imageId}_${gridSize}"
        val entry = RankEntry(uid, imageId, gridSize, time, moves, System.currentTimeMillis())

        db.collection("leaderboard")
            .document(docId)
            .set(entry)
            .await()
    }

    suspend fun getTopRanks(gridSize: Int): List<RankEntry> {
        val snapshots = db.collection("leaderboard")
            .whereEqualTo("gridSize", gridSize)
            .orderBy("bestTime")
            .limit(20)
            .get()
            .await()

        return snapshots.documents.mapNotNull { it.toObject(RankEntry::class.java) }
    }

    suspend fun getRankForGrid(gridSize: Int): Int {
        val uid = authManager.ensureSignedIn()

        val snapshots = db.collection("leaderboard")
            .whereEqualTo("gridSize", gridSize)
            .orderBy("bestTime")
            .get()
            .await()

        val list = snapshots.documents
        return list.indexOfFirst { it.getString("uid") == uid } + 1
    }
}