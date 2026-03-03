package com.example.picturepuzzle.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.picturepuzzle.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

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

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.bestScore.observe(viewLifecycleOwner) { score ->
            if (score != null) {
                val minutes = score.bestTime / 60
                val seconds = score.bestTime % 60
                binding.textBestTime.text = "Best Time: ${String.format("%02d:%02d", minutes, seconds)}"
                binding.textBestMoves.text = "Best Moves: ${score.bestMoves}"
            } else {
                binding.textBestTime.text = "Best Time: --:--"
                binding.textBestMoves.text = "Best Moves: --"
            }
        }

        viewModel.totalGames.observe(viewLifecycleOwner) { total ->
            binding.textTotalGames.text = "Total Games: ${total ?: 0}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}