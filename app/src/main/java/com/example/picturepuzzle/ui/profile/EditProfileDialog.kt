package com.example.picturepuzzle.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.picturepuzzle.R
import com.example.picturepuzzle.data.model.UserProfile
import com.example.picturepuzzle.databinding.FragmentEditProfileDialogBinding

class EditProfileDialog(
    private val currentProfile: UserProfile,
    private val onSaveClicked: (nickname: String, bio: String, avatarUrl: String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentEditProfileDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Điền dữ liệu hiện tại vào dialog
        binding.editNickname.setText(currentProfile.nickname)
        binding.editBio.setText(currentProfile.bio)
        binding.editAvatar.setText(currentProfile.avatarUrl)

        // Xử lý nút Save
        binding.btnSave.setOnClickListener {
            val nickname = binding.editNickname.text.toString().trim()
            val bio = binding.editBio.text.toString().trim()
            val avatarUrl = binding.editAvatar.text.toString().trim()

            onSaveClicked(nickname, bio, avatarUrl)
            dismiss()
        }

        // Xử lý nút Cancel
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}