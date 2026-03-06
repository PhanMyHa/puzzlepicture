package com.example.picturepuzzle.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturepuzzle.data.model.ImageCategory
import com.example.picturepuzzle.data.model.ImageItem
import com.example.picturepuzzle.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _selectedCategory = MutableLiveData<ImageCategory>(ImageCategory.NATURE)
    val selectedCategory: LiveData<ImageCategory> = _selectedCategory

    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    private val _completedImageIds = MutableLiveData<Set<Int>>()
    val completedImageIds: LiveData<Set<Int>> = _completedImageIds

    val completedCount: LiveData<Int> = imageRepository.getCompletedCount()

    init {
        loadCompletedImages()
        loadImages()
    }

    fun selectCategory(category: ImageCategory) {
        _selectedCategory.value = category
        loadImages()
    }

    private fun loadImages() {
        val category = _selectedCategory.value ?: ImageCategory.NATURE
        _images.value = imageRepository.getImagesByCategory(category)
    }

    private fun loadCompletedImages() {
        viewModelScope.launch {
            val ids = imageRepository.getCompletedImageIds()
            _completedImageIds.value = ids.toSet()
        }
    }

    fun refreshCompletedImages() {
        loadCompletedImages()
    }
}