package com.idz.travelconnect.features.post

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.idz.travelconnect.databinding.FragmentPostBinding
import java.util.Calendar

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        try {
            val bitmap = loadScaledBitmap(uri) ?: return@registerForActivityResult
            binding.ivSelectedImage.setImageBitmap(bitmap)
            binding.ivSelectedImage.visibility = View.VISIBLE
            binding.photoPlaceholder.visibility = View.GONE
            viewModel.onImageSelected(bitmap)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to load image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadScaledBitmap(uri: Uri): Bitmap? {
        val resolver = requireContext().contentResolver
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
        options.inJustDecodeBounds = false
        return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
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

        binding.etStartDate.setOnClickListener { showDatePicker(binding.etStartDate) }
        binding.etEndDate.setOnClickListener { showDatePicker(binding.etEndDate) }

        binding.btnShare.setOnClickListener {
            val destination = binding.etDestination.text.toString()
            val country = binding.etCountry.text.toString()
            val startDate = binding.etStartDate.text.toString()
            val endDate = binding.etEndDate.text.toString()
            val description = binding.etDescription.text.toString()
            viewModel.createPost(destination, country, startDate, endDate, description)
        }
    }

    private fun showDatePicker(target: TextView) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                target.text = "%02d/%02d/%d".format(day, month + 1, year)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                binding.etDestination.text?.clear()
                binding.etCountry.text?.clear()
                binding.etStartDate.text = ""
                binding.etEndDate.text = ""
                binding.etDescription.text?.clear()
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
