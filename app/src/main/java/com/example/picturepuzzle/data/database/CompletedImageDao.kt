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

    @Query("SELECT * FROM completed_images WHERE imageId = :imageId")
    fun getCompletedImage(imageId: Int): LiveData<CompletedImageEntity?>

    @Query("SELECT * FROM completed_images")
    fun getAllCompletedImages(): LiveData<List<CompletedImageEntity>>

    @Query("SELECT imageId FROM completed_images")
    suspend fun getCompletedImageIds(): List<Int>

    @Query("SELECT COUNT(*) FROM completed_images")
    fun getCompletedCount(): LiveData<Int>

    @Query("DELETE FROM completed_images WHERE imageId = :imageId")
    suspend fun deleteCompletedImage(imageId: Int)
}