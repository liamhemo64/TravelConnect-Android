package com.idz.travelconnect.features.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.travelconnect.R
import com.idz.travelconnect.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private val args: FeedFragmentArgs by navArgs()
    private var binding: FragmentFeedBinding? = null
    private val viewModel: FeedViewModel by viewModels()
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = PostsAdapter(
            onPostClick = { post ->
                println(post.id)
//                val action = FeedFragmentDirections.actionFeedFragmentToPostDetailFragment(post.id)
//                findNavController().navigate(action)
            }
        )
        binding?.rvPosts?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvPosts?.adapter = adapter
    }

    private fun setupListeners() {
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
            binding?.tvEmpty?.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding?.rvPosts?.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (!loading) binding?.swipeRefresh?.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
