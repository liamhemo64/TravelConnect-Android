package com.idz.travelconnect

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.idz.travelconnect.databinding.ActivityMainBinding
import com.idz.travelconnect.databinding.LayoutBottomNavBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: LayoutBottomNavBinding
    private lateinit var navController: NavController

    private val bottomNavDestinations = setOf(
        R.id.feedFragment,
        R.id.aiAssistantFragment,
        R.id.postFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav = LayoutBottomNavBinding.bind(binding.bottomNav.root)

        setupBottomNavClicks()
        setupNavListener()
    }

    private fun setupBottomNavClicks() {
        bottomNav.navHome.setOnClickListener { navigateTo(R.id.feedFragment) }
        bottomNav.btnAi.setOnClickListener { navigateTo(R.id.aiAssistantFragment) }
        bottomNav.navPost.setOnClickListener { navigateTo(R.id.postFragment) }
        bottomNav.navProfile.setOnClickListener { navigateTo(R.id.profileFragment) }
    }

    private fun navigateTo(destinationId: Int) {
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build()
        navController.navigate(destinationId, null, navOptions)
    }

    private fun setupNavListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isMainDest = destination.id in bottomNavDestinations
            binding.bottomNav.root.visibility = if (isMainDest) View.VISIBLE else View.GONE
            if (isMainDest) updateNavSelection(destination.id)
        }
    }

    private fun updateNavSelection(destinationId: Int) {
        val activeColor = ColorStateList.valueOf(getColor(R.color.nav_active))
        val inactiveColor = ColorStateList.valueOf(getColor(R.color.nav_inactive))

        bottomNav.iconHome.imageTintList = inactiveColor
        bottomNav.labelHome.setTextColor(getColor(R.color.nav_inactive))
        bottomNav.iconPost.imageTintList = inactiveColor
        bottomNav.labelPost.setTextColor(getColor(R.color.nav_inactive))
        bottomNav.iconProfile.imageTintList = inactiveColor
        bottomNav.labelProfile.setTextColor(getColor(R.color.nav_inactive))

        when (destinationId) {
            R.id.feedFragment -> {
                bottomNav.iconHome.imageTintList = activeColor
                bottomNav.labelHome.setTextColor(getColor(R.color.nav_active))
            }
            R.id.postFragment -> {
                bottomNav.iconPost.imageTintList = activeColor
                bottomNav.labelPost.setTextColor(getColor(R.color.nav_active))
            }
            R.id.profileFragment -> {
                bottomNav.iconProfile.imageTintList = activeColor
                bottomNav.labelProfile.setTextColor(getColor(R.color.nav_active))
            }
            // aiAssistantFragment: FAB is always gradient, label is always blue — no change needed
        }
    }
}
