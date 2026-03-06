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

        arguments?.let { args ->
            val imageRes = args.getInt("imageRes", -1)
            val imageId = args.getInt("imageId", -1)
            val gridSize = args.getInt("gridSize", 3)

            if (imageRes != -1 && imageId != -1) {
                viewModel.setCurrentImageId(imageId)

                val bitmap = BitmapFactory.decodeResource(resources, imageRes)
                viewModel.setImageBitmap(bitmap, gridSize)

                binding.buttonGridSize.visibility = View.GONE
            } else {
                loadDefaultImage()

                binding.buttonGridSize.visibility = View.VISIBLE
            }

        } ?: run {
            loadDefaultImage()
            binding.buttonGridSize.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        tileAdapter = TileAdapter { position ->
            soundManager.playClickSound()
            viewModel.rotateTile(position)
        }

        binding.recyclerViewTiles.apply {
            layoutManager = GridLayoutManager(requireContext(), 6).apply {
                setHasFixedSize(true)
            }
            adapter = tileAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            itemAnimator = null
            setPadding(0, 0, 0, 0)
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun setupObservers() {
        viewModel.tiles.observe(viewLifecycleOwner) { tiles ->
            tileAdapter.submitList(tiles)
        }

        viewModel.hintTiles.observe(viewLifecycleOwner) { hints ->
            tileAdapter.setHintTiles(hints)
        }

        viewModel.wrongTilesCount.observe(viewLifecycleOwner) { count ->
            binding.textWrongTiles.text = "Wrong: $count"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.recyclerViewTiles.isVisible = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) { seconds ->
            val minutes = seconds / 60
            val secs = seconds % 60
            binding.textTimer.text = String.format("%02d:%02d", minutes, secs)
        }

        viewModel.moves.observe(viewLifecycleOwner) { moves ->
            binding.textMoves.text = "Moves: $moves"
        }

        viewModel.isGameWon.observe(viewLifecycleOwner) { isWon ->
            if (isWon) {

            }
        }

        viewModel.gridSize.observe(viewLifecycleOwner) { size ->
            (binding.recyclerViewTiles.layoutManager as? GridLayoutManager)?.spanCount = size
        }

        viewModel.playWinSound.observe(viewLifecycleOwner) { play ->
            if (play) {
                soundManager.playWinSound()
            }
        }

        viewModel.zoomImage.observe(viewLifecycleOwner) { zoom ->
            if (zoom) {
                animateImageZoom()
            }
        }

        viewModel.showWinDialog.observe(viewLifecycleOwner) { show ->
            if (show) {
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

        binding.buttonGridSize.setOnClickListener {
            showGridSizeDialog()
        }
    }

    private fun loadDefaultImage() {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.game1
        )
        viewModel.setImageBitmap(bitmap, 6)
    }

    private fun loadImageFromUri(uri: Uri) {
        var loadedBitmap: Bitmap? = null

        try {
            loadedBitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(
                    requireContext().contentResolver,
                    uri
                )
                android.graphics.ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }

            viewModel.setImageBitmap(loadedBitmap, viewModel.gridSize.value ?: 3)

            loadedBitmap?.recycle()

        } catch (e: IOException) {
            e.printStackTrace()
            loadedBitmap?.recycle()
            showError("Failed to load image: ${e.message}")
        }
    }

    private fun showGridSizeDialog() {
        val sizes = arrayOf("3x3", "4x4", "5x5", "6x6", "7x7", "8x8")
        val currentGridSize = viewModel.gridSize.value ?: 3
        val currentIndex = currentGridSize - 3

        AlertDialog.Builder(requireContext())
            .setTitle("Select Grid Size")
            .setSingleChoiceItems(sizes, currentIndex) { dialog, which ->
                val gridSize = which + 3
                val currentBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.game1
                )
                viewModel.setImageBitmap(currentBitmap, gridSize)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun animateImageZoom() {
        binding.recyclerViewTiles.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(300)
            .withEndAction {
                binding.recyclerViewTiles.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }

    private fun showWinDialog() {
        val time = viewModel.timer.value ?: 0L
        val moves = viewModel.moves.value ?: 0
        val gridSize = viewModel.gridSize.value ?: 3

        val dialog = WinDialogFragment.newInstance(
            time = time,
            moves = moves,
            gridSize = gridSize,
            onNewGame = {
                viewModel.resetGame()
            },
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

    private fun recreateAdapter(bitmap: Bitmap, gridSize: Int) {
        tileAdapter = TileAdapter { position ->
            soundManager.playClickSound()
            viewModel.rotateTile(position)
        }
        binding.recyclerViewTiles.adapter = tileAdapter

        viewModel.tiles.value?.let { tiles ->
            tileAdapter.submitList(tiles)
        }

        (binding.recyclerViewTiles.layoutManager as? GridLayoutManager)?.spanCount = gridSize
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundManager.release()
        _binding = null
    }
}