package com.example.picturepuzzle.data.repository

import androidx.lifecycle.LiveData
import com.example.picturepuzzle.R
import com.example.picturepuzzle.data.database.CompletedImageDao
import com.example.picturepuzzle.data.database.CompletedImageEntity
import com.example.picturepuzzle.data.model.ImageCategory
import com.example.picturepuzzle.data.model.ImageItem

class ImageRepository(private val completedImageDao: CompletedImageDao) {

    fun getAllImages(): List<ImageItem> {
        return listOf(
            // Nature
            ImageItem(1, ImageCategory.NATURE, R.drawable.nature_1, "Mountain Lake"),
            ImageItem(2, ImageCategory.NATURE, R.drawable.nature_2, "Forest Path"),
            ImageItem(3, ImageCategory.NATURE, R.drawable.nature_3, "Sunset Beach"),
            ImageItem(4, ImageCategory.NATURE, R.drawable.nature_4, "Autumn Trees"),
            ImageItem(5, ImageCategory.NATURE, R.drawable.nature_5, "Waterfall"),

            // Animals
            ImageItem(6, ImageCategory.ANIMALS, R.drawable.animal_1, "Tiger"),
            ImageItem(7, ImageCategory.ANIMALS, R.drawable.animal_2, "Elephant"),
            ImageItem(8, ImageCategory.ANIMALS, R.drawable.animal_3, "Butterfly"),
            ImageItem(9, ImageCategory.ANIMALS, R.drawable.animal_4, "Eagle"),
            ImageItem(10, ImageCategory.ANIMALS, R.drawable.animal_5, "Dolphin"),

            // Art
            ImageItem(11, ImageCategory.ART, R.drawable.art_1, "Abstract 1"),
            ImageItem(12, ImageCategory.ART, R.drawable.art_2, "Abstract 2"),
            ImageItem(13, ImageCategory.ART, R.drawable.art_3, "Geometric"),
            ImageItem(14, ImageCategory.ART, R.drawable.art_4, "Colorful"),
            ImageItem(15, ImageCategory.ART, R.drawable.art_5, "Modern Art"),



            // Food
            ImageItem(21, ImageCategory.FOOD, R.drawable.food_1, "Sushi"),
            ImageItem(22, ImageCategory.FOOD, R.drawable.food_2, "Pizza"),
            ImageItem(23, ImageCategory.FOOD, R.drawable.food_3, "Fruits"),
            ImageItem(24, ImageCategory.FOOD, R.drawable.food_4, "Cake"),
            ImageItem(25, ImageCategory.FOOD, R.drawable.food_5, "Coffee"),
        )
    }

    fun getImagesByCategory(category: ImageCategory): List<ImageItem> {
        return getAllImages().filter { it.category == category }
    }

    fun getImageById(id: Int): ImageItem? {
        return getAllImages().find { it.id == id }
    }

    suspend fun markImageCompleted(imageId: Int, time: Long, moves: Int, gridSize: Int) {
        val entity = CompletedImageEntity(
            imageId = imageId,
            completedAt = System.currentTimeMillis(),
            bestTime = time,
            bestMoves = moves,
            gridSize = gridSize
        )
        completedImageDao.insertCompletedImage(entity)
    }

    suspend fun getCompletedImageIds(): List<Int> {
        return completedImageDao.getCompletedImageIds()
    }

    fun getCompletedImage(imageId: Int): LiveData<CompletedImageEntity?> {
        return completedImageDao.getCompletedImage(imageId)
    }

    fun getAllCompletedImages(): LiveData<List<CompletedImageEntity>> {
        return completedImageDao.getAllCompletedImages()
    }

    fun getCompletedCount(): LiveData<Int> {
        return completedImageDao.getCompletedCount()
    }
}