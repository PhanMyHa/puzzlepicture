package com.example.picturepuzzle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.picturepuzzle.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NavHost
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment

        navController = navHostFragment.navController

        // Bottom nav
        bottomNavigation = binding.bottomNavigation

        // KẾT NỐI bottom nav với navigation graph
        bottomNavigation.setupWithNavController(navController)

        // ẩn hiện bottom nav
        setupDestinationListener()
    }

    private fun setupDestinationListener() {

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                // màn hình login → ẩn menu
                R.id.authFragment -> {
                    bottomNavigation.visibility = View.GONE
                    supportActionBar?.hide()
                }

                // màn hình chơi game → ẩn menu
                R.id.gameFragment -> {
                    bottomNavigation.visibility = View.GONE
                }

                // các màn hình chính → hiện menu
                R.id.galleryFragment,
                R.id.profileFragment,
                R.id.leaderboardFragment,
                R.id.settingsFragment -> {

                    bottomNavigation.visibility = View.VISIBLE
                    supportActionBar?.show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}