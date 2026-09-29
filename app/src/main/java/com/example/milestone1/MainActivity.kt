package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signInButton = findViewById(R.id.signInButton)
        signUpButton = findViewById(R.id.signUpButton)

        signInButton.setOnClickListener {
            authenticate(isSignUp = false)
        }

        signUpButton.setOnClickListener {
            authenticate(isSignUp = true)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            openProfile()
        }
    }

    private fun authenticate(isSignUp: Boolean) {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter your email and password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (isSignUp && password.length < 6) {
            passwordInput.error = "Use at least 6 characters"
            return
        }

        signInButton.isEnabled = false
        signUpButton.isEnabled = false

        val task = if (isSignUp) {
            auth.createUserWithEmailAndPassword(email, password)
        } else {
            auth.signInWithEmailAndPassword(email, password)
        }

        task.addOnCompleteListener(this) { result ->
            signInButton.isEnabled = true
            signUpButton.isEnabled = true

            if (result.isSuccessful) {
                openProfile()
            } else {
                Toast.makeText(
                    this,
                    result.exception?.localizedMessage
                        ?: "Authentication failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}