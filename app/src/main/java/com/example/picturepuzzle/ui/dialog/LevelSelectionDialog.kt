package com.example.picturepuzzle.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.picturepuzzle.databinding.DialogLevelSelectionBinding

class LevelSelectionDialog : DialogFragment() {

    private var _binding: DialogLevelSelectionBinding? = null
    private val binding get() = _binding!!

    private var onLevelSelected: ((Int) -> Unit)? = null
    private var imageRes: Int = 0
    private var imageId: Int = 0
    private var imageName: String = ""
    private var completedLevels: Set<Int> = emptySet()

    companion object {
        private const val ARG_IMAGE_RES = "image_res"
        private const val ARG_IMAGE_ID = "image_id"
        private const val ARG_IMAGE_NAME = "image_name"
        private const val ARG_COMPLETED_LEVELS = "completed_levels"

        fun newInstance(
            imageRes: Int,
            imageId: Int,
            imageName: String,
            completedLevels: Set<Int>,
            onLevelSelected: (Int) -> Unit
        ): LevelSelectionDialog {
            return LevelSelectionDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RES, imageRes)
                    putInt(ARG_IMAGE_ID, imageId)
                    putString(ARG_IMAGE_NAME, imageName)
                    putIntegerArrayList(ARG_COMPLETED_LEVELS, ArrayList(completedLevels))
                }
                this.onLevelSelected = onLevelSelected
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLevelSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageRes = arguments?.getInt(ARG_IMAGE_RES) ?: 0
        imageId = arguments?.getInt(ARG_IMAGE_ID) ?: 0
        imageName = arguments?.getString(ARG_IMAGE_NAME) ?: ""
        completedLevels = arguments?.getIntegerArrayList(ARG_COMPLETED_LEVELS)?.toSet() ?: emptySet()

        setupViews()
        setupListeners()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun setupViews() {
        binding.imagePreview.setImageResource(imageRes)
        binding.textImageName.text = imageName

        updateLevelButton(binding.buttonEasy, 3, "🟢 Easy (3x3)")
        updateLevelButton(binding.buttonMedium, 4, "🟡 Medium (4x4)")
        updateLevelButton(binding.buttonHard, 5, "🔴 Hard (5x5)")
        updateLevelButton(binding.buttonExpert, 6, "⚫ Expert (6x6)")
    }

    private fun updateLevelButton(button: android.widget.Button, gridSize: Int, baseText: String) {
        button.text = if (completedLevels.contains(gridSize)) "$baseText  ✅" else baseText
    }

    private fun setupListeners() {
        binding.buttonEasy.setOnClickListener {
            onLevelSelected?.invoke(3)
            dismiss()
        }

        binding.buttonMedium.setOnClickListener {
            onLevelSelected?.invoke(4)
            dismiss()
        }

        binding.buttonHard.setOnClickListener {
            onLevelSelected?.invoke(5)
            dismiss()
        }

        binding.buttonExpert.setOnClickListener {
            onLevelSelected?.invoke(6)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}