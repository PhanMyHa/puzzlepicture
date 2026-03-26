package com.example.picturepuzzle.ui.game

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picturepuzzle.R
import com.example.picturepuzzle.databinding.FragmentGameBinding
import com.example.picturepuzzle.ui.dialog.WinDialogFragment
import com.example.picturepuzzle.utils.SoundManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels()
    private lateinit var tileAdapter: TileAdapter
    private lateinit var soundManager: SoundManager

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { loadImageFromUri(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        soundManager = SoundManager(requireContext())

        setupRecyclerView()
        setupObservers()
        setupListeners()

        setupBackPressHandler()
        hideBottomNav()

        arguments?.let { args ->
            val imageRes = args.getInt("imageRes", -1)
            val imageId = args.getInt("imageId", -1)
            val gridSize = args.getInt("gridSize", 3)

            if (imageRes != -1 && imageId != -1) {
                viewModel.setCurrentImageId(imageId)

                val bitmap = BitmapFactory.decodeResource(resources, imageRes)
                viewModel.setImageBitmap(bitmap, gridSize)
            } else {
                loadDefaultImage()
            }

        } ?: run {
            loadDefaultImage()
        }
    }

    private fun setupRecyclerView() {
        tileAdapter = TileAdapter { position ->
            soundManager.playClickSound()
            viewModel.rotateTile(position)
        }

        binding.recyclerViewTiles.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = tileAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            itemAnimator = null
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun setupObservers() {
        viewModel.tiles.observe(viewLifecycleOwner) {
            tileAdapter.submitList(it)
        }

        viewModel.hintTiles.observe(viewLifecycleOwner) {
            tileAdapter.setHintTiles(it)
        }

        viewModel.remainingHints.observe(viewLifecycleOwner) { count ->
            binding.buttonHint.text = "💡 Hint ($count)"
            binding.buttonHint.isEnabled = count > 0
        }

        viewModel.wrongTilesCount.observe(viewLifecycleOwner) {
            binding.textWrongTiles.text = "Wrong: $it"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
            binding.recyclerViewTiles.isVisible = !it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) { seconds ->
            val minutes = seconds / 60
            val secs = seconds % 60
            binding.textTimer.text = String.format("%02d:%02d", minutes, secs)
        }

        viewModel.moves.observe(viewLifecycleOwner) {
            binding.textMoves.text = "Moves: $it"
        }

        viewModel.gridSize.observe(viewLifecycleOwner) {
            (binding.recyclerViewTiles.layoutManager as? GridLayoutManager)?.spanCount = it
        }

        viewModel.playWinSound.observe(viewLifecycleOwner) {
            if (it) soundManager.playWinSound()
        }

        viewModel.zoomImage.observe(viewLifecycleOwner) {
            if (it) animateImageZoom()
        }

        viewModel.showWinDialog.observe(viewLifecycleOwner) {
            if (it) {
                showWinDialog()
                viewModel.onWinDialogShown()
            }
        }
    }

    private fun setupListeners() {
        binding.buttonHint.setOnClickListener {
            viewModel.showHint()
        }

        binding.buttonRestart.setOnClickListener {
            viewModel.resetGame()
        }

        binding.buttonSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonExitGame.setOnClickListener {
            showExitConfirmDialog()
        }
    }

    // EXIT

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit game?")
            .setMessage("Are you sure you want to leave this game?")
            .setPositiveButton("Exit") { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmDialog()
                }
            }
        )
    }

    private fun hideBottomNav() {
        val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
        bottomNav?.isVisible = false
    }

    private fun showBottomNav() {
        val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
        bottomNav?.isVisible = true
    }

    // =========================================

    private fun loadDefaultImage() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.game1)
        viewModel.setImageBitmap(bitmap, 6)
    }

    private fun loadImageFromUri(uri: Uri) {
        var bitmap: Bitmap? = null

        try {
            bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(
                    requireContext().contentResolver,
                    uri
                )
                android.graphics.ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }

            viewModel.setImageBitmap(bitmap, viewModel.gridSize.value ?: 3)

        } catch (e: IOException) {
            bitmap?.recycle()
            showError("Failed to load image: ${e.message}")
        }
    }

    private fun animateImageZoom() {
        binding.recyclerViewTiles.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(300)
            .withEndAction {
                binding.recyclerViewTiles.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }

    private fun showWinDialog() {
        val dialog = WinDialogFragment.newInstance(
            time = viewModel.timer.value ?: 0L,
            moves = viewModel.moves.value ?: 0,
            gridSize = viewModel.gridSize.value ?: 3,
            onNewGame = { viewModel.resetGame() },
            onClose = {
                findNavController().navigate(
                    R.id.action_gameFragment_to_galleryFragment
                )
            }
        )
        dialog.show(childFragmentManager, "WinDialog")
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNav()
        soundManager.release()
        _binding = null
    }
}