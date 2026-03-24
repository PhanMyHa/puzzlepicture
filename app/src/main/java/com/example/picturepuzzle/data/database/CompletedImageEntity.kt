package com.example.picturepuzzle.data.database

import androidx.room.Entity

@Entity(
    tableName = "completed_images",
    primaryKeys = ["imageId", "gridSize"]
)
data class CompletedImageEntity(
    val imageId: Int,
    val gridSize: Int,
    val completedAt: Long,
    val bestTime: Long,
    val bestMoves: Int
)