package com.example.picturepuzzle.ui.game

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.picturepuzzle.R
import com.example.picturepuzzle.data.database.AppDatabase
import com.example.picturepuzzle.data.database.ScoreEntity
import com.example.picturepuzzle.data.repository.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoreRepository

    private val _tiles = MutableLiveData<List<Tile>>()
    val tiles: LiveData<List<Tile>> = _tiles

    private val _timer = MutableLiveData<Long>(0L)
    val timer: LiveData<Long> = _timer

    private val _moves = MutableLiveData<Int>(0)
    val moves: LiveData<Int> = _moves

    private val _isGameWon = MutableLiveData<Boolean>(false)
    val isGameWon: LiveData<Boolean> = _isGameWon

    private val _gridSize = MutableLiveData<Int>(3)
    val gridSize: LiveData<Int> = _gridSize

    private var timerJob: Job? = null
    private var isTimerRunning = false

    private var selectedBitmap: Bitmap? = null
    private var isProcessingClick = false

    init {
        val scoreDao = AppDatabase.getDatabase(application).scoreDao()
        repository = ScoreRepository(scoreDao)
    }

    fun setImageBitmap(bitmap: Bitmap, gridSize: Int = 3) {
        // Crop to square first
        val squareBitmap = cropToSquare(bitmap)
        selectedBitmap = squareBitmap
        _gridSize.value = gridSize
        initializeGame()
    }

    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val size = minOf(width, height)

        val x = (width - size) / 2
        val y = (height - size) / 2

        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    fun initializeGame() {
        val bitmap = selectedBitmap ?: run {
            val defaultBitmap = BitmapFactory.decodeResource(
                getApplication<Application>().resources,
                R.drawable.game1
            )
            val squareBitmap = cropToSquare(defaultBitmap)
            selectedBitmap = squareBitmap
            squareBitmap
        }

        val size = _gridSize.value ?: 3
        val tileBitmaps = splitBitmap(bitmap, size)
        val tileList = mutableListOf<Tile>()
        val rotations = listOf(0, 90, 180, 270)

        for (i in tileBitmaps.indices) {
            val randomRotation = rotations.random()
            tileList.add(
                Tile(
                    id = i,
                    bitmap = tileBitmaps[i],
                    currentRotation = randomRotation,
                    correctRotation = 0
                )
            )
        }

        _tiles.value = tileList
        _timer.value = 0L
        _moves.value = 0
        _isGameWon.value = false
        isProcessingClick = false
        startTimer()
    }

    private fun splitBitmap(bitmap: Bitmap, gridSize: Int): List<Bitmap> {
        val tiles = mutableListOf<Bitmap>()
        val squareSize = bitmap.width // Bitmap đã là hình vuông
        val tileSize = squareSize / gridSize

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val x = col * tileSize
                val y = row * tileSize

                // Đảm bảo không vượt quá bounds
                val actualWidth = minOf(tileSize, squareSize - x)
                val actualHeight = minOf(tileSize, squareSize - y)

                val tileBitmap = Bitmap.createBitmap(
                    bitmap,
                    x,
                    y,
                    actualWidth,
                    actualHeight
                )
                tiles.add(tileBitmap)
            }
        }

        return tiles
    }

    private fun startTimer() {
        if (isTimerRunning) return
        isTimerRunning = true

        timerJob = viewModelScope.launch {
            while (isTimerRunning) {
                delay(1000L)
                _timer.value = (_timer.value ?: 0L) + 1L
            }
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        timerJob?.cancel()
    }

    fun rotateTile(position: Int) {
        if (_isGameWon.value == true || isProcessingClick) return

        isProcessingClick = true

        val currentList = _tiles.value?.toMutableList() ?: return
        if (position < 0 || position >= currentList.size) {
            isProcessingClick = false
            return
        }

        val tile = currentList[position]

        // Tạo bản sao mới với rotation updated
        val updatedTile = tile.copy(currentRotation = (tile.currentRotation + 90) % 360)
        currentList[position] = updatedTile

        _tiles.value = currentList
        _moves.value = (_moves.value ?: 0) + 1

        viewModelScope.launch {
            delay(200) // Đợi animation
            isProcessingClick = false
            checkWin()
        }
    }

    private fun checkWin() {
        val allTilesCorrect = _tiles.value?.all {
            it.currentRotation == it.correctRotation
        } ?: false

        if (allTilesCorrect) {
            _isGameWon.value = true
            stopTimer()
            saveScore()
        }
    }

    private fun saveScore() {
        viewModelScope.launch {
            val score = ScoreEntity(
                bestTime = _timer.value ?: 0L,
                bestMoves = _moves.value ?: 0,
                date = System.currentTimeMillis()
            )
            repository.insertScore(score)
        }
    }

    fun resetGame() {
        stopTimer()
        initializeGame()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        selectedBitmap?.recycle()
    }
}