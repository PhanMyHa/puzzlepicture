package com.example.picturepuzzle.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CompletedImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedImage(image: CompletedImageEntity)

    @Query("SELECT * FROM completed_images WHERE imageId = :imageId AND gridSize = :gridSize")
    fun getCompletedImage(imageId: Int, gridSize: Int): LiveData<CompletedImageEntity?>

    @Query("SELECT * FROM completed_images")
    fun getAllCompletedImages(): LiveData<List<CompletedImageEntity>>

    @Query("SELECT DISTINCT imageId FROM completed_images")
    suspend fun getCompletedImageIds(): List<Int>

    @Query("SELECT gridSize FROM completed_images WHERE imageId = :imageId")
    suspend fun getCompletedLevels(imageId: Int): List<Int>

    @Query("SELECT COUNT(*) FROM completed_images")
    fun getCompletedCount(): LiveData<Int>

    @Query("DELETE FROM completed_images WHERE imageId = :imageId AND gridSize = :gridSize")
    suspend fun deleteCompletedImage(imageId: Int, gridSize: Int)
}