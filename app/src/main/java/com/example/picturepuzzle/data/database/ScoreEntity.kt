package com.example.picturepuzzle.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bestTime: Long,
    val bestMoves: Int,
    val date: Long
)