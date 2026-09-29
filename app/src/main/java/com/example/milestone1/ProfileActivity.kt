package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var saveButton: Button

    private var profileListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user == null) {
            openLogin()
            return
        }

        nameInput = findViewById(R.id.nameInput)
        addressInput = findViewById(R.id.addressInput)
        phoneInput = findViewById(R.id.phoneInput)
        saveButton = findViewById(R.id.saveButton)

        findViewById<TextView>(R.id.emailText).text = user.email

        val database = FirebaseDatabase.getInstance(
            "https://milestone-1-d35a9-default-rtdb.firebaseio.com"
        )

        userRef = database.getReference("users").child(user.uid)
        saveButton.isEnabled = false

        saveButton.setOnClickListener {
            saveProfile()
        }

        findViewById<Button>(R.id.signOutButton).setOnClickListener {
            stopListening()
            auth.signOut()
            openLogin()
        }
    }

    override fun onStart() {
        super.onStart()

        if (!::userRef.isInitialized || auth.currentUser == null) {
            return
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nameInput.setText(
                    snapshot.child("name").getValue(String::class.java).orEmpty()
                )
                addressInput.setText(
                    snapshot.child("address").getValue(String::class.java).orEmpty()
                )
                phoneInput.setText(
                    snapshot.child("phone").getValue(String::class.java).orEmpty()
                )
                saveButton.isEnabled = true
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Could not load profile: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        profileListener = listener
        userRef.addValueEventListener(listener)
    }

    private fun saveProfile() {
        val name = nameInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(
                this,
                "Please fill in all profile fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val profile = mapOf(
            "name" to name,
            "address" to address,
            "phone" to phone
        )

        saveButton.isEnabled = false

        userRef.setValue(profile).addOnCompleteListener(this) { task ->
            saveButton.isEnabled = true

            val message = if (task.isSuccessful) {
                "Profile saved"
            } else {
                "Save failed: ${task.exception?.localizedMessage}"
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun stopListening() {
        profileListener?.let { listener ->
            userRef.removeEventListener(listener)
        }
        profileListener = null
    }

    override fun onStop() {
        stopListening()
        super.onStop()
    }

    private fun openLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}