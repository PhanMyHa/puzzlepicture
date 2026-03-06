package com.example.picturepuzzle.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picturepuzzle.R
import com.example.picturepuzzle.data.model.ImageCategory
import com.example.picturepuzzle.databinding.FragmentGalleryBinding
import com.example.picturepuzzle.ui.dialog.LevelSelectionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategoryChips()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCompletedImages()
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter { imageItem ->
            showLevelSelectionDialog(imageItem)
        }

        binding.recyclerViewImages.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = imageAdapter
        }
    }

    private fun setupCategoryChips() {
        binding.chipNature.setOnClickListener {
            viewModel.selectCategory(ImageCategory.NATURE)
        }

        binding.chipAnimals.setOnClickListener {
            viewModel.selectCategory(ImageCategory.ANIMALS)
        }

        binding.chipArt.setOnClickListener {
            viewModel.selectCategory(ImageCategory.ART)
        }

        binding.chipArchitecture.setOnClickListener {
            viewModel.selectCategory(ImageCategory.ARCHITECTURE)
        }

        binding.chipFood.setOnClickListener {
            viewModel.selectCategory(ImageCategory.FOOD)
        }
    }

    private fun setupObservers() {
        viewModel.images.observe(viewLifecycleOwner) { images ->
            imageAdapter.submitList(images)
        }

        viewModel.completedImageIds.observe(viewLifecycleOwner) { completedIds ->
            imageAdapter.setCompletedImages(completedIds)
        }

        viewModel.completedCount.observe(viewLifecycleOwner) { count ->
            binding.textCompletedCount.text = "Completed: $count/25"
        }
    }

    private fun showLevelSelectionDialog(imageItem: com.example.picturepuzzle.data.model.ImageItem) {
        val dialog = LevelSelectionDialog.newInstance(
            imageRes = imageItem.imageRes,
            imageId = imageItem.id,
            imageName = imageItem.name
        ) { gridSize ->
            navigateToGame(imageItem.imageRes, imageItem.id, gridSize)
        }

        dialog.show(childFragmentManager, "LevelSelection")
    }

    private fun navigateToGame(imageRes: Int, imageId: Int, gridSize: Int) {
        val bundle = Bundle().apply {
            putInt("imageRes", imageRes)
            putInt("imageId", imageId)
            putInt("gridSize", gridSize)
        }
        findNavController().navigate(R.id.action_galleryFragment_to_gameFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}