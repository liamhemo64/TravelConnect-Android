package com.idz.travelconnect.features.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.idz.travelconnect.R
import com.idz.travelconnect.data.model.auth.FirebaseAuthModel
import com.idz.travelconnect.databinding.FragmentProfileBinding
import com.idz.travelconnect.features.posts.PostListFragment
import com.squareup.picasso.Picasso
import kotlin.getValue

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    private val viewModel: ProfileViewModel by viewModels()
    private val authModel = FirebaseAuthModel()

    private var isEditing = false
    private var selectedAvatarBitmap: Bitmap? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { loadAvatarFromUri(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupPostList(savedInstanceState)
        setupObservers()
    }

    private fun setupListeners() {
        binding?.btnEditProfile?.setOnClickListener {
            if (!isEditing) {
                enterEditMode()
            } else {
                val displayName = binding?.etDisplayName?.text?.toString() ?: ""
                viewModel.updateProfile(displayName, selectedAvatarBitmap)
            }
        }

        binding?.ivEditAvatar?.setOnClickListener {
            if (isEditing) pickImageLauncher.launch("image/*")
        }

        binding?.btnSignOut?.setOnClickListener {
            viewModel.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun setupPostList(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val userId = authModel.currentUser?.uid
            
            // Create the fragment with userId argument using SafeArgs Bundle
            val postListFragment = PostListFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", userId)
                }
            }
            
            childFragmentManager.beginTransaction()
                .replace(R.id.postListContainer, postListFragment)
                .commit()
        }
    }

    private fun enterEditMode() {
        isEditing = true
        binding?.apply {
            etDisplayName.isFocusable = true
            etDisplayName.isFocusableInTouchMode = true
            etDisplayName.requestFocus()

            ivEditAvatar.visibility = View.VISIBLE
            btnEditProfile.text = getString(R.string.save)

            etDisplayName.setSelection(etDisplayName.text?.length ?: 0)
        }
    }

    private fun exitEditMode() {
        isEditing = false
        binding?.apply {
            etDisplayName.isFocusable = false
            etDisplayName.isFocusableInTouchMode = false
            etDisplayName.clearFocus()

            ivEditAvatar.visibility = View.GONE
            btnEditProfile.text = getString(R.string.edit_profile)
        }
        selectedAvatarBitmap = null
    }

    private fun loadAvatarFromUri(uri: Uri) {
        try {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
            selectedAvatarBitmap = bitmap
            binding?.ivAvatar?.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Snackbar.make(requireView(), "Failed to load image", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            binding?.etDisplayName?.setText(user.displayName)
            binding?.tvEmail?.text = user.email

            val b = binding ?: return@observe
            if (!user.avatarUrl.isNullOrBlank()) {
                Picasso.get()
                    .load(user.avatarUrl)
                    .fit()
                    .centerCrop()
                    .into(b.ivAvatar)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding?.progressBar?.visibility = if (loading) View.VISIBLE else View.GONE
            binding?.btnEditProfile?.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                viewModel.error.value = null
            }
        }

        viewModel.profileUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                Snackbar.make(requireView(), getString(R.string.profile_updated), Snackbar.LENGTH_SHORT).show()
                exitEditMode()
                viewModel.profileUpdated.value = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
