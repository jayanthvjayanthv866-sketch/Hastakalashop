package com.example.hastakalashop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.hastakalashop.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

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

        // Initialize View Binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logo Fade Animation
        val fadeInLogo = AnimationUtils.loadAnimation(
            this,
            android.R.anim.fade_in
        )

        fadeInLogo.duration = 1500

        binding.imgLogo.startAnimation(fadeInLogo)

        // App Name Fade Animation
        val fadeInText = AnimationUtils.loadAnimation(
            this,
            android.R.anim.fade_in
        )

        fadeInText.duration = 2000

        binding.txtAppName.startAnimation(fadeInText)

        // Open Login Screen After 3 Seconds
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finish()

        }, 3000)
    }
}