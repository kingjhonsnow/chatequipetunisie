package com.example.chatequipetunisie.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    private lateinit var ivUser: ShapeableImageView
    private lateinit var layoutTextInputEmail: TextInputLayout
    private lateinit var layoutTextInputName: TextInputLayout
    private lateinit var btnSave: MaterialButton
    private var isImageChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        ivUser = findViewById(R.id.ivUser)
        layoutTextInputEmail = findViewById(R.id.layoutTextInputEmail)
        layoutTextInputName = findViewById(R.id.layoutTextInputName)
        btnSave = findViewById(R.id.btnSave)

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                Glide.with(this).load(uri).placeholder(R.drawable.ahmed).into(ivUser)
                isImageChanged = true
            }
        }

        ivUser.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        currentUser?.let { user ->
            fetchAndDisplayUserData(user)
        } ?: run {
            Log.d("SettingsActivity", "No user found")
            // Handle the case where no user is logged in. Perhaps navigate to login screen.
        }
    }

    private fun fetchAndDisplayUserData(user: FirebaseUser) {
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                document?.toObject(User::class.java)?.let { fetchedUser ->
                    fetchedUser.uuid = user.uid
                    setUserData(fetchedUser)
                } ?: run {
                    Log.e("SettingsActivity", "User document found, but couldn't parse to User object.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SettingsActivity", "Failed to fetch user data: ${exception.message}")
                Toast.makeText(this, "Failed to load user data. Please try again later.", Toast.LENGTH_LONG).show()
            }
    }

    private fun setUserData(user: User) {
        layoutTextInputEmail.editText?.setText(user.email)
        layoutTextInputName.editText?.setText(user.fullName)

        user.image?.let {
            loadImageFromUrl(it)
        }

        btnSave.setOnClickListener {
            if (validateInput()) {
                if (isImageChanged) {
                    updateImageAndUserInfo(user)
                } else {
                    updateUserInfo(user)
                }
            }
        }
    }

    private fun updateImageAndUserInfo(user: User) {
        val bitmap = (ivUser.drawable as BitmapDrawable).bitmap
        val byteArray = bitmapToByteArray(bitmap)

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("user_images/${user.uuid}.jpg")

        val uploadTask = imageRef.putBytes(byteArray)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                user.image = uri.toString() // Store image URL in Firestore
                updateUserInfo(user)
            }.addOnFailureListener { exception ->
                Log.e("SettingsActivity", "Failed to get image URL: ${exception.message}")
                Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("SettingsActivity", "Failed to upload image: ${exception.message}")
            Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_LONG).show()
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) // Compress with 50% quality
        return baos.toByteArray()
    }

    private fun loadImageFromUrl(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ahmed) // Placeholder image while loading
            .into(ivUser)
    }

    private fun updateUserInfo(user: User) {
        val updatedUser: HashMap<String, Any> = hashMapOf(
            "fullname" to layoutTextInputName.editText?.text.toString(),
            "image" to (user.image ?: "")
        )

        Log.d("SettingsActivity", "Updating user data: Fullname: ${layoutTextInputName.editText?.text.toString()}, Image: ${user.image}")

        db.collection("users").document(user.uuid).update(updatedUser)
            .addOnSuccessListener {
                Toast.makeText(this, "Your information has been updated.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                layoutTextInputName.error = "Error updating information. Please try again later."
                layoutTextInputName.isErrorEnabled = true
                Log.e("SettingsActivity", "User update error: ${exception.message}")
            }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        if (layoutTextInputName.editText?.text.toString().isEmpty()) {
            layoutTextInputName.error = "Name cannot be empty"
            layoutTextInputName.isErrorEnabled = true
            isValid = false
        } else {
            layoutTextInputName.isErrorEnabled = false
        }
        return isValid
    }
}
