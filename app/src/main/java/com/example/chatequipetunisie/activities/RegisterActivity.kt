package com.example.chatequipetunisie.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatequipetunisie.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnRegister: MaterialButton
    private lateinit var layoutTextInputName: TextInputLayout
    private lateinit var layoutTextInputEmail: TextInputLayout
    private lateinit var layoutTextInputPassword: TextInputLayout
    private lateinit var layoutTextInputConfirmPassword: TextInputLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnRegister = findViewById(R.id.btnRegister)
        layoutTextInputName = findViewById(R.id.layoutTextInputName)
        layoutTextInputEmail = findViewById(R.id.layoutTextInputEmail)
        layoutTextInputPassword = findViewById(R.id.layoutTextInputPassword)
        layoutTextInputConfirmPassword = findViewById(R.id.layoutTextInputConfirmPassword)

        btnRegister.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
        // Réinitialiser les erreurs
        initErrors()

        val name = layoutTextInputName.editText?.text.toString().trim()
        val email = layoutTextInputEmail.editText?.text.toString().trim()
        val password = layoutTextInputPassword.editText?.text.toString().trim()
        val confirmPassword = layoutTextInputConfirmPassword.editText?.text.toString().trim()

        if (name.isEmpty()) {
            layoutTextInputName.error = "Le nom est requis !"
        }

        if (email.isEmpty()) {
            layoutTextInputEmail.error = "L'email est requis !"
        }

        if (password.isEmpty()) {
            layoutTextInputPassword.error = "Le mot de passe est requis !"
        }

        if (confirmPassword.isEmpty()) {
            layoutTextInputConfirmPassword.error = "La confirmation du mot de passe est requise !"
        }

        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            layoutTextInputConfirmPassword.error = "Les mots de passe ne correspondent pas !"
            return
        }

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
            // Enregistrement utilisateur Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = hashMapOf(
                            "fullname" to name,
                            "email" to email
                        )

                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            db.collection("user").document(currentUser.uid) // Changement ici
                                .set(user)
                                .addOnSuccessListener {
                                    // Naviguer vers HomeActivity en cas de succès
                                    Intent(this, HomeActivity::class.java).also {
                                        startActivity(it)
                                        finish()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    handleFirestoreError(e)
                                }
                        } else {
                            layoutTextInputConfirmPassword.error = "Erreur : utilisateur non disponible."
                        }
                    } else {
                        handleFirebaseError(task.exception)
                    }
                }
        }
    }

    private fun initErrors() {
        layoutTextInputEmail.isErrorEnabled = false
        layoutTextInputName.isErrorEnabled = false
        layoutTextInputPassword.isErrorEnabled = false
        layoutTextInputConfirmPassword.isErrorEnabled = false
    }

    private fun handleFirebaseError(exception: Exception?) {
        when (exception) {
            is FirebaseNetworkException -> {
                layoutTextInputEmail.error = "Erreur réseau : vérifiez votre connexion Internet."
            }
            is FirebaseAuthException -> {
                layoutTextInputEmail.error = "Erreur d'authentification : ${exception.localizedMessage}"
            }
            else -> {
                layoutTextInputEmail.error = "Une erreur inattendue est survenue : ${exception?.localizedMessage}"
            }
        }
    }

    private fun handleFirestoreError(exception: Exception) {
        when (exception) {
            is FirebaseNetworkException -> {
                layoutTextInputConfirmPassword.error = "Erreur réseau : impossible de connecter à Firestore."
            }
            else -> {
                layoutTextInputConfirmPassword.error = "Erreur Firestore : ${exception.localizedMessage}"
            }
        }
    }
}
