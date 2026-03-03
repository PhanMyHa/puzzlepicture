package com.example.picturepuzzle.ui.game

import android.graphics.Bitmap

data class Tile(
    val id: Int,
    val bitmap: Bitmap,
    var currentRotation: Int = 0,
    val correctRotation: Int = 0
)