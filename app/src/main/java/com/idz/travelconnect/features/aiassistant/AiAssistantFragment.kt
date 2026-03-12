package com.idz.travelconnect.features.aiassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.idz.travelconnect.databinding.FragmentAiAssistantBinding

class AiAssistantFragment : Fragment() {

    private var binding: FragmentAiAssistantBinding? = null
    private val viewModel: AiAssistantViewModel by viewModels()
    private lateinit var adapter: AiResponseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAiAssistantBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = AiResponseAdapter()
        binding?.rvResponses?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvResponses?.adapter = adapter
    }

    private fun setupListeners() {
        binding?.btnGetResponse?.setOnClickListener {
            val query = binding?.etQuestion?.text?.toString()?.trim() ?: ""
            if (query.isBlank()) {
                binding?.etQuestion?.error = "Please enter a question"
                return@setOnClickListener
            }
            viewModel.getAiResponse(query)
            binding?.etQuestion?.setText("")
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding?.progressBar?.visibility = if (loading) View.VISIBLE else View.GONE
            binding?.btnGetResponse?.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.responses.observe(viewLifecycleOwner) { responses ->
            adapter.submitList(responses)
            val count = responses.size
            binding?.tvSavedResponsesHeader?.text =
                "Your Saved Responses ($count)"
            binding?.tvSavedResponsesHeader?.visibility =
                if (count > 0) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
