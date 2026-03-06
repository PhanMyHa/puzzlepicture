package com.example.picturepuzzle.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_images")
data class CompletedImageEntity(
    @PrimaryKey
    val imageId: Int,
    val completedAt: Long,
    val bestTime: Long,
    val bestMoves: Int,
    val gridSize: Int
)