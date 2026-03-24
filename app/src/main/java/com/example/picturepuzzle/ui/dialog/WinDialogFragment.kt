package com.example.picturepuzzle.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.example.picturepuzzle.R
import com.example.picturepuzzle.databinding.DialogWinBinding

class WinDialogFragment : DialogFragment() {

    private var _binding: DialogWinBinding? = null
    private val binding get() = _binding!!

    private var onNewGameClick: (() -> Unit)? = null
    private var onCloseClick: (() -> Unit)? = null

    companion object {
        private const val ARG_TIME = "time"
        private const val ARG_MOVES = "moves"
        private const val ARG_GRID_SIZE = "grid_size"

        fun newInstance(
            time: Long,
            moves: Int,
            gridSize: Int,
            onNewGame: () -> Unit,
            onClose: () -> Unit
        ): WinDialogFragment {
            return WinDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TIME, time)
                    putInt(ARG_MOVES, moves)
                    putInt(ARG_GRID_SIZE, gridSize)
                }
                this.onNewGameClick = onNewGame
                this.onCloseClick = onClose
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val time = arguments?.getLong(ARG_TIME) ?: 0L
        val moves = arguments?.getInt(ARG_MOVES) ?: 0
        val gridSize = arguments?.getInt(ARG_GRID_SIZE) ?: 3

        setupViews(time, moves, gridSize)
        setupAnimations()
        setupListeners()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
        }
    }

    private fun setupViews(time: Long, moves: Int, gridSize: Int) {
        val minutes = time / 60
        val seconds = time % 60

        binding.textTime.text = String.format("%02d:%02d", minutes, seconds)
        binding.textMoves.text = "$moves moves"
        binding.textGridSize.text = "${gridSize}x${gridSize} grid"


        binding.lottieAnimation.playAnimation()
    }

    private fun setupAnimations() {
        val bounceAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        binding.contentContainer.startAnimation(bounceAnim)

        binding.textCongrats.alpha = 0f
        binding.textCongrats.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        binding.statsContainer.alpha = 0f
        binding.statsContainer.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(400)
            .start()
    }

    private fun setupListeners() {
        binding.buttonNewGame.setOnClickListener {
            onNewGameClick?.invoke()
            dismiss()
        }

        binding.buttonClose.setOnClickListener {
            onCloseClick?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}