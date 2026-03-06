package com.example.picturepuzzle.data.model

import androidx.annotation.DrawableRes

data class ImageItem(
    val id: Int,
    val category: ImageCategory,
    @DrawableRes val imageRes: Int,
    val name: String,
    val difficulty: Difficulty = Difficulty.MEDIUM
) {
    enum class Difficulty {
        EASY, MEDIUM, HARD
    }
}