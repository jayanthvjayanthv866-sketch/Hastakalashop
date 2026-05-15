package com.example.hastakalashop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.hastakalashop.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {

        // Load Theme
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val theme = prefs.getInt("theme", R.style.Theme_HastaKalaShop_Royal)
        setTheme(theme)

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Toggle Login / Signup
        binding.txtToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        // Login Button Click
        binding.btnLogin.setOnClickListener {

            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val confirmPassword =
                binding.edtConfirmPassword.text.toString().trim()

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // LOGIN MODE
            if (isLoginMode) {

                val savedUser =
                    sharedPref.getString("username", "")

                val savedPass =
                    sharedPref.getString("password", "")

                // Default Admin Login
                val isAdmin =
                    username == "admin" && password == "admin"

                // Registered User Login
                val isRegisteredUser =
                    username == savedUser &&
                            password == savedPass

                if (isAdmin || isRegisteredUser) {

                    Toast.makeText(
                        this,
                        "Welcome to Hasta Kala Shop!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Invalid Username or Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                // SIGNUP MODE

                if (confirmPassword.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please confirm password",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (password != confirmPassword) {

                    Toast.makeText(
                        this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                // Save User Data
                sharedPref.edit {
                    putString("username", username)
                    putString("password", password)
                }

                Toast.makeText(
                    this,
                    "Registration Successful!",
                    Toast.LENGTH_SHORT
                ).show()

                // Switch to Login Mode
                isLoginMode = true
                updateUI()
            }
        }
    }

    // Update UI for Login / Signup
    private fun updateUI() {

        if (isLoginMode) {

            binding.txtLoginTitle.text = "Welcome Back"
            binding.txtLoginSub.text = "Login to manage your shop"

            binding.edtConfirmPassword.visibility = View.GONE

            binding.btnLogin.text = "LOGIN"

            binding.txtToggleMode.text =
                "Don't have an account? Sign Up"

        } else {

            binding.txtLoginTitle.text = "Create Account"
            binding.txtLoginSub.text =
                "Sign up to start your artisan journey"

            binding.edtConfirmPassword.visibility =
                View.VISIBLE

            binding.btnLogin.text = "SIGN UP"

            binding.txtToggleMode.text =
                "Already have an account? Login"
        }
    }
}