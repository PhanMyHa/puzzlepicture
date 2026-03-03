package com.example.picturepuzzle.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.picturepuzzle.data.database.AppDatabase
import com.example.picturepuzzle.data.database.ScoreEntity
import com.example.picturepuzzle.data.repository.ScoreRepository

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoreRepository

    val bestScore: LiveData<ScoreEntity?>
    val totalGames: LiveData<Int>

    init {
        val scoreDao = AppDatabase.getDatabase(application).scoreDao()
        repository = ScoreRepository(scoreDao)
        bestScore = repository.bestScore
        totalGames = repository.totalGames
    }
}