package com.example.picturepuzzle.ui.game

import android.graphics.Bitmap

data class Tile(
    val id: Int,
    val bitmap: Bitmap,
    var currentRotation: Int = 0,
    val correctRotation: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tile

        if (id != other.id) return false
        if (currentRotation != other.currentRotation) return false
        if (correctRotation != other.correctRotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + currentRotation
        result = 31 * result + correctRotation
        return result
    }


}