package com.example.picturepuzzle.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.picturepuzzle.R
import com.example.picturepuzzle.data.firebase.FriendRepository
import com.example.picturepuzzle.databinding.FragmentProfileBinding
import com.example.picturepuzzle.utils.DailyCheckInManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var friendRepository: FriendRepository

    private lateinit var friendAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        loadAllData()
    }

    // ====================== SETUP UI ======================
    private fun setupUI() {
        setupRecyclerView()
        setupButtons()
        setupDailyCheckIn()
    }

    private fun setupRecyclerView() {
        friendAdapter = FriendAdapter(emptyList())
        binding.recyclerFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendAdapter
        }
    }

    private fun setupButtons() {
        binding.btnEditProfile.setOnClickListener { showEditProfileDialog() }
        binding.btnLogout.setOnClickListener { showLogoutConfirmation() }
    }

    private fun setupDailyCheckIn() {
        val checkInManager = DailyCheckInManager(requireContext())
        val result = checkInManager.checkInIfNeeded()
        binding.textStreak.text = "Streak: ${result.streak} days"
        binding.textBestStreak.text = "Best: ${result.bestStreak} days"
    }

    // ====================== OBSERVERS ======================
    private fun setupObservers() {
        observeProfile()
        observeScore()
    }

    private fun observeProfile() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            binding.tvNickname.text = profile.nickname.ifBlank { "No nickname" }
            binding.tvBio.text = profile.bio.ifBlank { "No bio yet" }

            Glide.with(this)
                .load(profile.avatarUrl.ifBlank { null })
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(binding.imgAvatar)
        }
    }

    private fun observeScore() {
        viewModel.bestScore.observe(viewLifecycleOwner) { score ->
            if (score != null) {
                val minutes = score.bestTime / 60
                val seconds = score.bestTime % 60
                binding.textBestTime.text = "🏆 Best Time: ${String.format("%02d:%02d", minutes, seconds)}"
                binding.textBestMoves.text = "🎯 Best Moves: ${score.bestMoves}"
            } else {
                binding.textBestTime.text = "🏆 Best Time: --:--"
                binding.textBestMoves.text = "🎯 Best Moves: --"
            }
        }

        viewModel.totalGames.observe(viewLifecycleOwner) { total ->
            binding.textTotalGames.text = "🎮 Total Games: ${total ?: 0}"
        }
    }

    // ====================== LOAD DATA ======================
    private fun loadAllData() {
        viewModel.loadProfile()

        lifecycleScope.launch {
            val rank = viewModel.getCurrentRank(3)
            binding.textRank.text = "Rank: #$rank"

            val friends = viewModel.getFriends()
            friendAdapter.updateData(friends)
        }
    }

    // ====================== DIALOGS ======================
    private fun showEditProfileDialog() {
        val currentProfile = viewModel.profile.value ?: return

        val dialog = EditProfileDialog(
            currentProfile = currentProfile,
            onSaveClicked = { nickname, bio, avatarUrl ->
                viewModel.updateProfile(nickname, bio, avatarUrl)
            }
        )

        dialog.show(parentFragmentManager, "EditProfileDialog")
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ -> performLogout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        viewModel.logout()

        val navController = findNavController()
        navController.navigate(R.id.action_main_nav_graph_to_auth_nav_graph)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}