package com.example.hastakalashop

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.hastakalashop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // Load Saved Theme
        val prefs = getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )

        val theme = prefs.getInt(
            "theme",
            R.style.Theme_HastaKalaShop_Royal
        )

        setTheme(theme)

        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Bottom Navigation Setup
        binding.bottomNavigation
            .setupWithNavController(navController)
    }
}