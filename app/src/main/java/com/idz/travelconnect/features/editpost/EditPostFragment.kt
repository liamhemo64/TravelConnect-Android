package com.idz.travelconnect.features.editpost

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.idz.travelconnect.databinding.FragmentEditPostBinding
import com.idz.travelconnect.model.Post
import java.text.SimpleDateFormat
import java.util.*

class EditPostFragment : Fragment() {

    private var binding: FragmentEditPostBinding? = null
    private val viewModel: EditPostViewModel by viewModels()
    private val args: EditPostFragmentArgs by navArgs()
    private var originalPost: Post? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(args.postId)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding?.etStartDate?.setOnClickListener { showDatePicker { date ->
            binding?.etStartDate?.setText(date)
        }}
        binding?.tilStartDate?.setEndIconOnClickListener { showDatePicker { date ->
            binding?.etStartDate?.setText(date)
        }}

        binding?.etEndDate?.setOnClickListener { showDatePicker { date ->
            binding?.etEndDate?.setText(date)
        }}
        binding?.tilEndDate?.setEndIconOnClickListener { showDatePicker { date ->
            binding?.etEndDate?.setText(date)
        }}

        binding?.btnSave?.setOnClickListener {
            val post = originalPost ?: return@setOnClickListener
            viewModel.updatePost(
                original = post,
                destination = binding?.etDestination?.text?.toString() ?: "",
                country = binding?.etCountry?.text?.toString() ?: "",
                startDate = binding?.etStartDate?.text?.toString() ?: "",
                endDate = binding?.etEndDate?.text?.toString() ?: "",
                description = binding?.etDescription?.text?.toString() ?: "",
                newImageBitmap = null
            )
        }
    }

    private fun showDatePicker(onDate: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDate(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private fun setupObservers() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            if (post != null && originalPost == null) {
                originalPost = post
                binding?.etDestination?.setText(post.destination)
                binding?.etCountry?.setText(post.country)
                binding?.etStartDate?.setText(post.startDate)
                binding?.etEndDate?.setText(post.endDate)
                binding?.etDescription?.setText(post.description)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding?.progressBar?.visibility = if (loading) View.VISIBLE else View.GONE
            binding?.btnSave?.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                viewModel.error.value = null
            }
        }

        viewModel.postUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                Snackbar.make(requireView(), "Trip updated!", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
