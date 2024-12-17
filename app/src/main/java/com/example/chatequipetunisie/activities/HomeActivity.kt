package com.example.chatequipetunisie.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.adapters.FriendsRecyclerAdapter
import com.example.chatequipetunisie.models.Friend
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date

class HomeActivity : AppCompatActivity() {
    lateinit var rvFriends: RecyclerView
    lateinit var fabChat: FloatingActionButton
    lateinit var friendsRecyclerAdapter: FriendsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_home)

        // Initialize views
        rvFriends = findViewById(R.id.rvfriends)
        fabChat = findViewById(R.id.fabChat)

        // FloatingActionButton click listener to open UsersSearchActivity
        fabChat.setOnClickListener {
            try {
                Intent(this, UsersSearchActivity::class.java).also {
                    startActivity(it)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception
                Toast.makeText(this, "Error starting UsersSearchActivity", Toast.LENGTH_SHORT).show()
            }
        }

        // Example list of friends
        val friends = mutableListOf(
            Friend("jaziri", "Hey, how are you?", "", Date().time),
            Friend("msakni", "See you tomorrow!", "", Date().time - 3600000),
            Friend("aymen dahmen", "Let's catch up later.", "", Date().time - 7200000)
        )

        // Set up RecyclerView for friends list
        friendsRecyclerAdapter = FriendsRecyclerAdapter()
        friendsRecyclerAdapter.items = friends
        rvFriends.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = friendsRecyclerAdapter
        }

        // Handle edge-to-edge insets for full-screen layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    // Inflate the menu from XML (menu_home.xml)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    // Handle the menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemSettings -> {
                // Open SettingsActivity
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.itemLogout -> {
                // Clear session (if needed) and navigate to AuthentificationActivity
                clearUserSession()

                // Navigate to AuthentificationActivity
                val intent = Intent(this, AuthentificationActivity::class.java)
                startActivity(intent)

                // Optional: Call finish() if you want to close the current activity and prevent back navigation
                finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearUserSession() {
        // Example of clearing session data using SharedPreferences (if you're storing data)
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Clears all data
        editor.apply()  // Apply changes asynchronously

        // Alternatively, clear specific data if you are storing tokens or user information
    }
}
