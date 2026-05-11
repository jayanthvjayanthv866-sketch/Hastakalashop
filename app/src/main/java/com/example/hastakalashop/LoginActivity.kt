package com.example.hastakalashop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hastakalashop.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE)
        val theme = prefs.getInt("theme", R.style.Theme_HastaKalaShop_Royal)
        setTheme(theme)
        
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        binding.txtToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) {
                // Login Logic
                val savedUser = sharedPref.getString("username", null)
                val savedPass = sharedPref.getString("password", null)

                // Fallback for first-time use with default admin/admin
                val isValidAdmin = username == "admin" && password == "admin"
                val isValidUser = username == savedUser && password == savedPass

                if (isValidAdmin || isValidUser) {
                    Toast.makeText(this, "Welcome to Hasta Kala Shop!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Registration Logic
                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                with(sharedPref.edit()) {
                    putString("username", username)
                    putString("password", password)
                    apply()
                }

                Toast.makeText(this, "Registration Successful! Please Login.", Toast.LENGTH_SHORT).show()
                isLoginMode = true
                updateUI()
            }
        }
    }

    private fun updateUI() {
        if (isLoginMode) {
            binding.txtLoginTitle.text = "Welcome Back"
            binding.txtLoginSub.text = "Login to manage your shop"
            binding.edtConfirmPassword.visibility = View.GONE
            binding.btnLogin.text = "LOGIN"
            binding.txtToggleMode.text = "Don't have an account? Sign Up"
        } else {
            binding.txtLoginTitle.text = "Create Account"
            binding.txtLoginSub.text = "Sign up to start your artisan journey"
            binding.edtConfirmPassword.visibility = View.VISIBLE
            binding.btnLogin.text = "SIGN UP"
            binding.txtToggleMode.text = "Already have an account? Login"
        }
    }
}
