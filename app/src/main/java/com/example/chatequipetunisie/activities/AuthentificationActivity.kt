package com.example.chatequipetunisie.activities

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatequipetunisie.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthentificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvRegister: TextView
    private lateinit var textEmail: TextInputLayout
    private lateinit var textPassword: TextInputLayout
    private lateinit var btnConnect: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification)

        // Initialize FirebaseAuth and views
        auth = Firebase.auth
        tvRegister = findViewById(R.id.tvRegister)
        textEmail = findViewById(R.id.textEmail)
        textPassword = findViewById(R.id.textPassword)
        btnConnect = findViewById(R.id.btnConnect)

        // Handle login button click
        btnConnect.setOnClickListener {
            dismissKeyboard() // Dismiss the keyboard on button click
            validateAndNavigate() // Validate input and attempt login
        }

        // Handle "Créer un compte" click to navigate to RegisterActivity
        tvRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun dismissKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun validateAndNavigate() {
        val email = textEmail.editText?.text.toString().trim()
        val password = textPassword.editText?.text.toString().trim()

        if (email.isEmpty()) {
            textEmail.error = "Please enter your email"
        } else if (password.isEmpty()) {
            textPassword.error = "Please enter your password"
        } else {
            textEmail.error = null // Clear previous error
            textPassword.error = null // Clear previous error

            // Attempt Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login successful, navigate to HomeActivity
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        Intent(this, HomeActivity::class.java).also {
                            startActivity(it)
                            finish() // Finish the current activity
                        }
                    } else {
                        // Login failed, show an error message
                        Toast.makeText(
                            this,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    // Method to navigate to RegisterActivity
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
