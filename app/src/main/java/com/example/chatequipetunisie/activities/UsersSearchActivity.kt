package com.example.chatequipetunisie.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.adapters.UsersRecyclerAdapter
import com.example.chatequipetunisie.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UsersSearchActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private lateinit var rvUsers: RecyclerView
    private lateinit var editSearch: EditText
    private lateinit var usersRecyclerAdapter: UsersRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_users_search)

        // Initialize Firebase instances
        auth = Firebase.auth
        currentUser = auth.currentUser

        // Ensure the user is authenticated
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish() // Redirect to login if needed
            return
        }

        // Initialize Views
        rvUsers = findViewById(R.id.rvUsers)
        editSearch = findViewById(R.id.editSearch)

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView and Adapter
        usersRecyclerAdapter = UsersRecyclerAdapter()
        rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersSearchActivity)
            adapter = usersRecyclerAdapter
        }

        // Fetch authenticated users and set them to the adapter
        fetchAuthenticatedUsers()

        // Search filter
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usersRecyclerAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })
    }

    // Fetch users from Firebase Authentication
    private fun fetchAuthenticatedUsers() {
        val users = mutableListOf<User>()

        // Check if the user is authenticated
        currentUser?.let {
            // Use the displayName or email as fallback for name
            val userName =
                it.displayName ?: "Unknown" // If displayName is null, set default "Unknown"
            val userEmail = it.email ?: "No Email" // If email is null, set default "No Email"

            // Create the User object and add it to the list
            val user = User(uuid = it.uid, fullName = userName, email = userEmail, image = "")
            users.add(user)


            // Set the users to the adapter (just the current authenticated user in this case)
            usersRecyclerAdapter.items = users
        }
    }
}