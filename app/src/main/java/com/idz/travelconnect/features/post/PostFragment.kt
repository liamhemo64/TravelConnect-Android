package com.idz.travelconnect.features.post

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.idz.travelconnect.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap?.let { bmp ->
                binding.ivSelectedImage.setImageBitmap(bmp)
                binding.ivSelectedImage.visibility = View.VISIBLE
                binding.photoPlaceholder.visibility = View.GONE
                viewModel.onImageSelected(bmp)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        setupView()
        observeViewModel()
        return binding.root
    }

    private fun setupView() {
        binding.btnChooseGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.ivSelectedImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnShare.setOnClickListener {
            val location = binding.etLocation.text.toString()
            val caption = binding.etCaption.text.toString()
            viewModel.createPost(location, caption)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnShare.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.postSuccessData.observe(viewLifecycleOwner) { postId ->
            postId?.let {
                Toast.makeText(requireContext(), "Post shared!", Toast.LENGTH_SHORT).show()
                binding.etLocation.text?.clear()
                binding.etCaption.text?.clear()
                binding.ivSelectedImage.visibility = View.GONE
                binding.photoPlaceholder.visibility = View.VISIBLE
                viewModel.clearPostSuccessData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
