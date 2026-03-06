package com.example.picturepuzzle.utils

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class ImageProcessor {


    suspend fun cropToSquare(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height


        if (width == height) {
            return@withContext bitmap
        }

        val size = minOf(width, height)
        val x = (width - size) / 2
        val y = (height - size) / 2

        Bitmap.createBitmap(bitmap, x, y, size, size)
    }


    suspend fun splitBitmap(
        bitmap: Bitmap,
        gridSize: Int
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        require(bitmap.width == bitmap.height) {
            "Bitmap must be square. Current size: ${bitmap.width}x${bitmap.height}"
        }
        require(gridSize > 0) { "Grid size must be positive" }

        val tiles = mutableListOf<Bitmap>()
        val bitmapSize = bitmap.width


        val exactTileSize = bitmapSize.toFloat() / gridSize.toFloat()

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val exactX = col * exactTileSize
                val exactY = row * exactTileSize
                val exactEndX = (col + 1) * exactTileSize
                val exactEndY = (row + 1) * exactTileSize

                val x = exactX.roundToInt()
                val y = exactY.roundToInt()
                val endX = exactEndX.roundToInt()
                val endY = exactEndY.roundToInt()

                val width = endX - x
                val height = endY - y


                val tileBitmap = Bitmap.createBitmap(bitmap, x, y, width, height)
                tiles.add(tileBitmap)
            }
        }


        verifyTiles(tiles, bitmapSize)

        tiles
    }

    private fun verifyTiles(tiles: List<Bitmap>, originalSize: Int) {
        val totalPixels = tiles.sumOf { it.width * it.height }
        val expectedPixels = originalSize * originalSize

        require(totalPixels == expectedPixels) {
            "Tile splitting failed: Total pixels ($totalPixels) != Expected pixels ($expectedPixels)"
        }
    }


    suspend fun resizeBitmap(
        bitmap: Bitmap,
        maxSize: Int = 1200
    ): Bitmap = withContext(Dispatchers.Default) {
        val currentSize = maxOf(bitmap.width, bitmap.height)

        if (currentSize <= maxSize) {
            return@withContext bitmap
        }

        val ratio = maxSize.toFloat() / currentSize.toFloat()
        val newWidth = (bitmap.width * ratio).roundToInt()
        val newHeight = (bitmap.height * ratio).roundToInt()

        // Tạo bitmap mới - KHÔNG recycle
        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private suspend fun adjustSizeForGrid(
        bitmap: Bitmap,
        gridSize: Int
    ): Bitmap = withContext(Dispatchers.Default) {
        val currentSize = bitmap.width
        val remainder = currentSize % gridSize

        if (remainder == 0) {
            return@withContext bitmap
        }

        // Điều chỉnh xuống kích thước chia hết
        val adjustedSize = currentSize - remainder

        // Tạo bitmap mới - KHÔNG recycle
        Bitmap.createScaledBitmap(bitmap, adjustedSize, adjustedSize, true)
    }


    suspend fun prepareImageForGame(
        originalBitmap: Bitmap,
        gridSize: Int,
        maxSize: Int = 1200
    ): PreparedImage = withContext(Dispatchers.Default) {

        val resized = resizeBitmap(originalBitmap, maxSize)

        val square = cropToSquare(resized)

        val adjusted = adjustSizeForGrid(square, gridSize)

        val tiles = splitBitmap(adjusted, gridSize)

        PreparedImage(
            originalBitmap = originalBitmap,
            resizedBitmap = if (resized != originalBitmap) resized else null,
            squareBitmap = if (square != resized && square != originalBitmap) square else null,
            adjustedBitmap = adjusted,
            tiles = tiles,
            gridSize = gridSize
        )
    }
}


data class PreparedImage(
    val originalBitmap: Bitmap,
    val resizedBitmap: Bitmap?,
    val squareBitmap: Bitmap?,
    val adjustedBitmap: Bitmap,
    val tiles: List<Bitmap>,
    val gridSize: Int
) {
    val tileSize: Int = adjustedBitmap.width / gridSize


    fun recycleIntermediateBitmaps() {
        resizedBitmap?.recycle()
        squareBitmap?.recycle()

    }


    fun recycleAll() {
        resizedBitmap?.recycle()
        squareBitmap?.recycle()
        adjustedBitmap.recycle()
        tiles.forEach { it.recycle() }

    }
}