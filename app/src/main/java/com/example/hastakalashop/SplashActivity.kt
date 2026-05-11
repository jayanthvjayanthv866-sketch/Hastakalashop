package com.example.hastakalashop

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
        val prefs = getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE)
        val theme = prefs.getInt("theme", R.style.Theme_HastaKalaShop_Royal)
        setTheme(theme)
        
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fade-in animations
        val fadeInLogo = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeInLogo.duration = 1500
        binding.imgLogo.startAnimation(fadeInLogo)

        val fadeInText = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeInText.duration = 2000
        binding.txtAppName.startAnimation(fadeInText)

        // Navigate to Login after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)
    }
}
