package com.example.picturepuzzle.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {

    @Insert
    suspend fun insertScore(score: ScoreEntity)

    @Query("SELECT * FROM scores ORDER BY bestTime ASC LIMIT 1")
    fun getBestScore(): LiveData<ScoreEntity?>

    @Query("SELECT COUNT(*) FROM scores")
    fun getTotalGames(): LiveData<Int>

    @Query("SELECT * FROM scores ORDER BY bestTime ASC")
    fun getAllScores(): LiveData<List<ScoreEntity>>
}