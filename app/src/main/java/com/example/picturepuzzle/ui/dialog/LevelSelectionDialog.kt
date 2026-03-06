package com.example.picturepuzzle.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.picturepuzzle.R
import com.example.picturepuzzle.databinding.DialogLevelSelectionBinding

class LevelSelectionDialog : DialogFragment() {

    private var _binding: DialogLevelSelectionBinding? = null
    private val binding get() = _binding!!

    private var onLevelSelected: ((Int) -> Unit)? = null
    private var imageRes: Int = 0
    private var imageId: Int = 0
    private var imageName: String = ""

    companion object {
        private const val ARG_IMAGE_RES = "image_res"
        private const val ARG_IMAGE_ID = "image_id"
        private const val ARG_IMAGE_NAME = "image_name"

        fun newInstance(
            imageRes: Int,
            imageId: Int,
            imageName: String,
            onLevelSelected: (Int) -> Unit
        ): LevelSelectionDialog {
            return LevelSelectionDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RES, imageRes)
                    putInt(ARG_IMAGE_ID, imageId)
                    putString(ARG_IMAGE_NAME, imageName)
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