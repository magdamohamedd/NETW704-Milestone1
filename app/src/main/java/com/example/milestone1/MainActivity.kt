package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var titleText: TextView
    private lateinit var registrationFields: View
    private lateinit var submitButton: Button
    private lateinit var switchModeButton: Button

    private var registrationMode = false
    private var pendingProfileUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            view.setPadding(
                bars.left, bars.top, bars.right, bars.bottom
            )
            insets
        }

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        nameInput = findViewById(R.id.registerNameInput)
        addressInput = findViewById(R.id.registerAddressInput)
        phoneInput = findViewById(R.id.registerPhoneInput)
        titleText = findViewById(R.id.titleText)
        registrationFields = findViewById(R.id.registrationFields)
        submitButton = findViewById(R.id.submitButton)
        switchModeButton = findViewById(R.id.switchModeButton)

        pendingProfileUid =
            savedInstanceState?.getString("pendingProfileUid")

        registrationMode =
            savedInstanceState?.getBoolean("registrationMode") ?: false

        if (pendingProfileUid != null) {
            registrationMode = true
        }

        updateScreen()
        setBusy(false)

        submitButton.setOnClickListener {
            authenticate()
        }

        switchModeButton.setOnClickListener {
            registrationMode = !registrationMode
            updateScreen()
        }

        if (auth.currentUser != null && pendingProfileUid == null) {
            openProfile()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("registrationMode", registrationMode)
        outState.putString("pendingProfileUid", pendingProfileUid)
        super.onSaveInstanceState(outState)
    }

    private fun updateScreen() {
        registrationFields.visibility =
            if (registrationMode) View.VISIBLE else View.GONE

        titleText.text =
            if (registrationMode) "Create your account"
            else "Sign in to Milestone1"

        submitButton.text = when {
            pendingProfileUid != null -> "Retry saving profile"
            registrationMode -> "Register"
            else -> "Sign In"
        }

        switchModeButton.text =
            if (registrationMode) "Back to Sign In"
            else "Create an account"
    }

    private fun authenticate() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val name = nameInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter your email and password")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Enter a valid email address"
            return
        }

        if (registrationMode) {
            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                showMessage("Please enter your name, address and phone number")
                return
            }

            if (password.length < 6) {
                passwordInput.error = "Use at least 6 characters"
                return
            }

            val profile = mapOf(
                "name" to name,
                "address" to address,
                "phone" to phone
            )

            setBusy(true)

            // Retry a failed profile save without creating another account.
            val pendingUid = pendingProfileUid
            if (pendingUid != null) {
                saveInitialProfile(pendingUid, profile)
                return
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid

                        if (uid == null) {
                            setBusy(false)
                            showMessage("Could not obtain the account ID")
                            return@addOnCompleteListener
                        }

                        pendingProfileUid = uid
                        saveInitialProfile(uid, profile)
                    } else {
                        setBusy(false)
                        showMessage(
                            task.exception?.localizedMessage
                                ?: "Registration failed"
                        )
                    }
                }
        } else {
            setBusy(true)

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    setBusy(false)

                    if (task.isSuccessful) {
                        openProfile()
                    } else {
                        showMessage(
                            task.exception?.localizedMessage
                                ?: "Sign-in failed"
                        )
                    }
                }
        }
    }

    private fun saveInitialProfile(
        uid: String,
        profile: Map<String, String>
    ) {
        val database = FirebaseDatabase.getInstance(
            "https://milestone-1-d35a9-default-rtdb.firebaseio.com"
        )

        database.getReference("users")
            .child(uid)
            .setValue(profile)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    pendingProfileUid = null
                    showMessage("Account created and profile saved")
                    openProfile()
                } else {
                    updateScreen()
                    setBusy(false)
                    showMessage(
                        "Account created, but profile save failed. " +
                                "Tap Retry saving profile. " +
                                (task.exception?.localizedMessage ?: "")
                    )
                }
            }
    }

    private fun setBusy(busy: Boolean) {
        submitButton.isEnabled = !busy
        switchModeButton.isEnabled =
            !busy && pendingProfileUid == null

        emailInput.isEnabled = !busy && pendingProfileUid == null
        passwordInput.isEnabled = !busy && pendingProfileUid == null
        nameInput.isEnabled = !busy
        addressInput.isEnabled = !busy
        phoneInput.isEnabled = !busy
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}
