package com.example.picturepuzzle.data.repository

import androidx.lifecycle.LiveData
import com.example.picturepuzzle.data.database.ScoreDao
import com.example.picturepuzzle.data.database.ScoreEntity

class ScoreRepository(private val scoreDao: ScoreDao) {

    val bestScore: LiveData<ScoreEntity?> = scoreDao.getBestScore()
    val totalGames: LiveData<Int> = scoreDao.getTotalGames()

    suspend fun insertScore(score: ScoreEntity) {
        scoreDao.insertScore(score)
    }
}