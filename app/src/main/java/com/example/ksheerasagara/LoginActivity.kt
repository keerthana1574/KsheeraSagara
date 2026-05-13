package com.example.ksheerasagara

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ksheerasagara.databinding.ActivityLoginBinding
import com.example.ksheerasagara.firebase.FirebaseAuthManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = FirebaseAuthManager(this)

        // If already logged in, go directly to MainActivity
        if (authManager.isSignedIn()) {
            goToMainActivity()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authManager.signInWithEmail(
                email = email,
                password = password,
                onSuccess = {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                },
                onFailure = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}