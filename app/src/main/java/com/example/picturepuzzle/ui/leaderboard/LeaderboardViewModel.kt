package com.example.picturepuzzle.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturepuzzle.data.firebase.RankEntry
import com.example.picturepuzzle.data.firebase.RankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val rankRepository: RankRepository
) : ViewModel() {

    private val _ranks = MutableLiveData<List<RankEntry>>()
    val ranks: LiveData<List<RankEntry>> = _ranks

    fun loadRank(gridSize: Int) {
        viewModelScope.launch {
            val list = rankRepository.getTopRanks(gridSize)
            _ranks.value = list
        }
    }
}