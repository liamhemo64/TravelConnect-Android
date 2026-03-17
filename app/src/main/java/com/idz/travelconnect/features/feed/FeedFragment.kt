package com.idz.travelconnect.features.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.idz.travelconnect.R
import com.idz.travelconnect.databinding.FragmentFeedBinding
import com.idz.travelconnect.features.posts.PostListFragment

class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null

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
        
        if (savedInstanceState == null) {
            val postListFragment = PostListFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.postListContainer, postListFragment)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
