package com.example.ksheerasagara

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ksheerasagara.databinding.ActivityRegisterBinding
import com.example.ksheerasagara.firebase.FirebaseAuthManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = FirebaseAuthManager(this)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authManager.createAccount(
                name = name,
                email = email,
                password = password,
                onSuccess = {
                    Toast.makeText(this, "Account created! Please login", Toast.LENGTH_LONG).show()
                    // Go back to Login screen
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvLogin.setOnClickListener {
            finish() // Go back to login screen
        }
    }
}