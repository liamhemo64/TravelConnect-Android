package com.idz.travelconnect.features.postdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.travelconnect.R
import com.idz.travelconnect.databinding.FragmentPostDetailsBinding
import com.idz.travelconnect.features.postdetail.comment.CommentsAdapter
import com.squareup.picasso.Picasso

class PostDetailFragment : Fragment() {

    private var binding: FragmentPostDetailsBinding? = null
    private val viewModel: PostDetailViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var commentsAdapter: CommentsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(args.postId)
        setupCommentsRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupCommentsRecyclerView() {
        commentsAdapter = CommentsAdapter(
            currentUserId = viewModel.uid,
            onDeleteClick = { comment ->
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.delete_comment))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        viewModel.deleteComment(comment.id)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        )
        binding?.rvComments?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvComments?.adapter = commentsAdapter
    }

    private fun setupListeners() {
        binding?.btnBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.btnSendComment?.setOnClickListener {
            val text = binding?.etComment?.text?.toString() ?: ""
            if (text.isNotBlank()) {
                viewModel.addComment(text)
                binding?.etComment?.setText("")
            }
        }

        binding?.btnEdit?.setOnClickListener {
            val action = PostDetailFragmentDirections
                .actionPostDetailFragmentToEditPostFragment(args.postId)
            findNavController().navigate(action)
        }

        binding?.btnDelete?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.delete_post_confirm))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    viewModel.deletePost()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    private fun setupObservers() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            post ?: return@observe
            binding?.tvLocation?.text = "${post.destination}, ${post.country}"
            binding?.tvUserName?.text = post.userName
            binding?.tvDates?.text = "${post.startDate} – ${post.endDate}"
            binding?.tvDescription?.text = post.description

            val b = binding ?: return@observe

            if (!post.imageUrl.isNullOrBlank()) {
                b.ivPostImage.visibility = View.VISIBLE
                Picasso.get()
                    .load(post.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(b.ivPostImage)
            }

            if (!post.userAvatarUrl.isNullOrBlank()) {
                Picasso.get()
                    .load(post.userAvatarUrl)
                    .fit()
                    .centerCrop()
                    .into(b.ivUserAvatar)
            }

            if (viewModel.isOwner(post)) {
                b.btnEdit.visibility = View.VISIBLE
                b.btnDelete.visibility = View.VISIBLE
            } else {
                b.btnEdit.visibility = View.GONE
                b.btnDelete.visibility = View.GONE
            }
        }

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsAdapter.submitList(comments)
            binding?.tvNoComments?.visibility = if (comments.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.postDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
