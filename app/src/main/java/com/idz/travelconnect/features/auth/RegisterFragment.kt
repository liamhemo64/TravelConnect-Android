package com.idz.travelconnect.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.idz.travelconnect.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val displayName = binding.etDisplayName.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString() ?: ""
            val confirm = binding.etConfirmPassword.text?.toString() ?: ""

            var isValid = true

            if (displayName.isBlank()) {
                binding.etDisplayName.error = "Display Name is required"
                isValid = false
            }

            if (email.isBlank()) {
                binding.etEmail.error = "Email is required"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Valid email is required"
                isValid = false
            }

            if (password.isBlank()) {
                binding.etPassword.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                binding.etPassword.error = "Password must be at least 6 characters"
                isValid = false
            }

            if (confirm.isBlank()) {
                binding.etConfirmPassword.error = "Confirm your password"
                isValid = false
            } else if (confirm != password) {
                binding.etConfirmPassword.error = "Passwords do not match"
                isValid = false
            }

            if (isValid) {
                viewModel.register(displayName, email, password, confirm)
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
            binding.btnGoToLogin.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.registerSuccessData.observe(viewLifecycleOwner) { email ->
            if (email != null) {
                // Navigate directly to Feed, bypassing Login
                val action = RegisterFragmentDirections.actionRegisterFragmentToFeedFragment(email)
                findNavController().navigate(action)
                viewModel.clearRegisterSuccessData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}