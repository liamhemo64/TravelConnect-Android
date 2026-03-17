package com.idz.travelconnect.features.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.travelconnect.NavGraphDirections
import com.idz.travelconnect.databinding.FragmentPostListBinding
import com.idz.travelconnect.features.feed.PostsAdapter

class PostListFragment : Fragment() {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PostListViewModel by viewModels()
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
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
                val action = NavGraphDirections.actionGlobalPostDetailFragment(post.id)
                activity?.let {
                    findNavController().navigate(action)
                }
            }
        )
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosts.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
            binding.tvEmpty.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding.rvPosts.visibility = if (posts.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
