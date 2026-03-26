package com.example.picturepuzzle.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.picturepuzzle.R
import com.example.picturepuzzle.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)

        binding.buttonLogin.setOnClickListener {
            viewModel.login(
                binding.editEmail.text.toString(),
                binding.editPassword.text.toString()
            )
        }

        binding.buttonRegister.setOnClickListener {
            viewModel.register(
                binding.editEmail.text.toString(),
                binding.editPassword.text.toString()
            )
        }

        binding.buttonAnonymous.setOnClickListener {
            viewModel.loginAnonymous()
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_authFragment_to_main_nav_graph)
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}