package com.example.picturepuzzle.ui.game

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.picturepuzzle.data.database.ScoreEntity
import com.example.picturepuzzle.data.firebase.RankRepository
import com.example.picturepuzzle.data.repository.ImageRepository
import com.example.picturepuzzle.data.repository.ScoreRepository
import com.example.picturepuzzle.utils.ImageProcessor
import com.example.picturepuzzle.utils.PreparedImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val imageRepository: ImageRepository,
    private val imageProcessor: ImageProcessor,
    private val rankRepository: RankRepository
) : ViewModel() {

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

    private val _wrongTilesCount = MutableLiveData<Int>(0)
    val wrongTilesCount: LiveData<Int> = _wrongTilesCount

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hintTiles = MutableLiveData<Set<Int>>()
    val hintTiles: LiveData<Set<Int>> = _hintTiles

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _showWinDialog = MutableLiveData<Boolean>(false)
    val showWinDialog: LiveData<Boolean> = _showWinDialog

    private val _playWinSound = MutableLiveData<Boolean>(false)
    val playWinSound: LiveData<Boolean> = _playWinSound

    private val _zoomImage = MutableLiveData<Boolean>(false)
    val zoomImage: LiveData<Boolean> = _zoomImage

    private val _sourceBitmap = MutableLiveData<Bitmap?>()
    val sourceBitmap: LiveData<Bitmap?> = _sourceBitmap

    private val _remainingHints = MutableLiveData<Int>(3)
    val remainingHints: LiveData<Int> = _remainingHints

    private var timerJob: Job? = null
    private var isTimerRunning = false
    private var isProcessingClick = false

    private var currentPreparedImage: PreparedImage? = null
    private val _currentImageId = MutableLiveData<Int?>()

    fun setImageBitmap(bitmap: Bitmap, gridSize: Int = 3) {

        _sourceBitmap.value = bitmap

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                Log.d("GameViewModel", "Starting image preparation")

                val prepared = imageProcessor.prepareImageForGame(bitmap, gridSize)

                currentPreparedImage?.recycleIntermediateBitmaps()

                currentPreparedImage = prepared
                _gridSize.value = gridSize

                initializeGame()

            } catch (e: Exception) {

                Log.e("GameViewModel", "Error preparing image", e)
                _error.value = e.message

            } finally {

                _isLoading.value = false

            }
        }
    }

    private fun initializeGame() {

        val prepared = currentPreparedImage ?: return

        val tileList = mutableListOf<Tile>()
        val rotations = listOf(0, 90, 180, 270)
        _remainingHints.value = 3

        for (i in prepared.tiles.indices) {

            val randomRotation = rotations.random()

            tileList.add(
                Tile(
                    id = i,
                    bitmap = prepared.tiles[i],
                    currentRotation = randomRotation,
                    correctRotation = 0
                )
            )
        }

        _tiles.value = tileList
        _timer.value = 0
        _moves.value = 0
        _isGameWon.value = false
        isProcessingClick = false

        updateWrongTilesCount()
        startTimer()
    }

    private fun startTimer() {

        if (isTimerRunning) return

        isTimerRunning = true

        timerJob = viewModelScope.launch {

            while (isTimerRunning) {

                delay(1000)

                _timer.value = (_timer.value ?: 0) + 1

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

        if (position !in currentList.indices) {

            isProcessingClick = false
            return

        }

        val tile = currentList[position]

        val updatedTile = tile.copy(
            currentRotation = (tile.currentRotation + 90) % 360
        )

        currentList[position] = updatedTile

        _tiles.value = currentList

        _moves.value = (_moves.value ?: 0) + 1

        viewModelScope.launch {

            delay(200)

            isProcessingClick = false

            updateWrongTilesCount()

            checkWin()

        }
    }

    private fun updateWrongTilesCount() {

        val wrongCount = _tiles.value?.count {
            it.currentRotation != it.correctRotation
        } ?: 0

        _wrongTilesCount.value = wrongCount

    }

    fun showHint() {
        val remaining = _remainingHints.value ?: 0
        if (remaining <= 0) return

        val wrongTiles = _tiles.value?.mapIndexedNotNull { index, tile ->
            if (tile.currentRotation != tile.correctRotation) index else null
        }?.toSet() ?: emptySet()

        _hintTiles.value = wrongTiles
        _remainingHints.value = remaining - 1

        viewModelScope.launch {
            delay(2000)
            _hintTiles.value = emptySet()
        }
    }

    private fun checkWin() {

        val allTilesCorrect = _tiles.value?.all {
            it.currentRotation == it.correctRotation
        } ?: false

        if (allTilesCorrect) {

            _isGameWon.value = true

            stopTimer()

            _playWinSound.value = true
            _zoomImage.value = true

            saveScore()

            viewModelScope.launch {

                delay(500)

                _showWinDialog.value = true

            }
        }
    }

    fun onWinDialogShown() {

        _showWinDialog.value = false
        _playWinSound.value = false
        _zoomImage.value = false

    }

    fun setCurrentImageId(imageId: Int?) {

        _currentImageId.value = imageId

    }

    private fun saveScore() {

        viewModelScope.launch {

            val score = ScoreEntity(
                bestTime = _timer.value ?: 0,
                bestMoves = _moves.value ?: 0,
                date = System.currentTimeMillis()
            )

            scoreRepository.insertScore(score)

            _currentImageId.value?.let { imageId ->

                imageRepository.markImageCompleted(
                    imageId = imageId,
                    time = _timer.value ?: 0,
                    moves = _moves.value ?: 0,
                    gridSize = _gridSize.value ?: 3
                )

                rankRepository.submitScore(
                    imageId = imageId,
                    gridSize = _gridSize.value ?: 3,
                    time = _timer.value ?: 0,
                    moves = _moves.value ?: 0
                )
            }
        }
    }

    fun resetGame() {

        stopTimer()
        initializeGame()

    }

    fun clearError() {

        _error.value = null

    }

    override fun onCleared() {

        super.onCleared()

        stopTimer()

        currentPreparedImage?.let { prepared ->

            prepared.tiles.forEach {

                if (!it.isRecycled) it.recycle()

            }

            prepared.recycleIntermediateBitmaps()
            prepared.adjustedBitmap.recycle()

        }

        currentPreparedImage = null
    }
}