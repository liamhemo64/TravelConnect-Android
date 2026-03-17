package com.idz.travelconnect.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.idz.travelconnect.R
import com.idz.travelconnect.data.model.auth.FirebaseAuthModel
import com.idz.travelconnect.databinding.FragmentProfileBinding
import com.idz.travelconnect.features.posts.PostListFragment
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authModel = FirebaseAuthModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostList(savedInstanceState)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
