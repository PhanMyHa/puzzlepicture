package com.example.picturepuzzle.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.picturepuzzle.data.database.AppDatabase
import com.example.picturepuzzle.data.database.ScoreEntity
import com.example.picturepuzzle.data.firebase.AuthRepository
import com.example.picturepuzzle.data.firebase.Friend
import com.example.picturepuzzle.data.firebase.FriendRepository
import com.example.picturepuzzle.data.firebase.ProfileRepository
import com.example.picturepuzzle.data.firebase.RankRepository
import com.example.picturepuzzle.data.model.UserProfile
import com.example.picturepuzzle.data.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository,
    private val friendRepo: FriendRepository,
    private val rankRepo: RankRepository
) : AndroidViewModel(application) {

    // ==================== Profile (Firebase) ====================
    private val _profile = MutableLiveData<UserProfile>()
    val profile: LiveData<UserProfile> = _profile

    // ==================== Score (Room) ====================
    private val scoreRepository: ScoreRepository
    val bestScore: LiveData<ScoreEntity?>
    val totalGames: LiveData<Int?>

    init {
        val scoreDao = AppDatabase.getDatabase(application).scoreDao()
        scoreRepository = ScoreRepository(scoreDao)
        bestScore = scoreRepository.bestScore
        totalGames = scoreRepository.totalGames as LiveData<Int?>
    }

    // Load profile từ Firebase
    fun loadProfile() {
        viewModelScope.launch {
            val uid = authRepo.currentUser()?.uid ?: return@launch
            val loadedProfile = profileRepo.getProfile(uid)
            _profile.value = loadedProfile ?: UserProfile(uid = uid)
        }
    }

    // Cập nhật profile
    fun updateProfile(nickname: String, bio: String, avatarUrl: String) {
        viewModelScope.launch {
            val uid = authRepo.currentUser()?.uid ?: return@launch
            val newProfile = UserProfile(uid, nickname, avatarUrl, bio)
            profileRepo.saveProfile(newProfile)
            _profile.value = newProfile
        }
    }

    // Logout
    fun logout() {
        authRepo.logout()
    }

    // ==================== Helper functions ====================
    suspend fun getCurrentRank(gridSize: Int = 3): Int {
        return rankRepo.getRankForGrid(gridSize)
    }

    suspend fun getFriends(): List<Friend> {
        return friendRepo.getFriends()
    }
}