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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picturepuzzle.R
import com.example.picturepuzzle.databinding.FragmentGameBinding
import java.io.IOException

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels()
    private lateinit var tileAdapter: TileAdapter

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

        setupRecyclerView()
        setupObservers()
        setupListeners()

        loadDefaultImage()
    }

    private fun setupRecyclerView() {
        tileAdapter = TileAdapter { position ->
            viewModel.rotateTile(position)
        }

        binding.recyclerViewTiles.apply {
            layoutManager = GridLayoutManager(requireContext(), 3).apply {
                setHasFixedSize(true)
                // Loại bỏ spacing giữa các items
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = 1
                }
            }
            adapter = tileAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            itemAnimator = null

            // Loại bỏ mọi decoration/divider mặc định
            setPadding(0, 0, 0, 0)
            clipToPadding = false

            // Loại bỏ overscroll effect
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun setupObservers() {
        viewModel.tiles.observe(viewLifecycleOwner) { tiles ->
            tileAdapter.submitList(tiles)
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
                showWinDialog()
            }
        }

        viewModel.gridSize.observe(viewLifecycleOwner) { size ->
            (binding.recyclerViewTiles.layoutManager as? GridLayoutManager)?.spanCount = size
        }
    }

    private fun setupListeners() {
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
        viewModel.setImageBitmap(bitmap, 3)
    }

    private fun loadImageFromUri(uri: Uri) {
        try {
            val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(
                    requireContext().contentResolver,
                    uri
                )
                android.graphics.ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }

            val maxSize = 1000
            val scaledBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
                val size = maxOf(bitmap.width, bitmap.height)
                val scale = maxSize.toFloat() / size
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true
                )
            } else {
                bitmap
            }

            viewModel.setImageBitmap(scaledBitmap, viewModel.gridSize.value ?: 3)

        } catch (e: IOException) {
            e.printStackTrace()
            showError("Failed to load image")
        }
    }

    private fun showGridSizeDialog() {
        val sizes = arrayOf("3x3", "4x4", "5x5", "6x6")
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

    private fun showWinDialog() {
        val time = viewModel.timer.value ?: 0L
        val moves = viewModel.moves.value ?: 0

        val minutes = time / 60
        val seconds = time % 60

        AlertDialog.Builder(requireContext())
            .setTitle("🎉 Puzzle Solved!")
            .setMessage("Time: ${String.format("%02d:%02d", minutes, seconds)}\nMoves: $moves")
            .setCancelable(false)
            .setPositiveButton("New Game") { _, _ ->
                viewModel.resetGame()
            }
            .setNegativeButton("Close", null)
            .show()
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
        _binding = null
    }
}