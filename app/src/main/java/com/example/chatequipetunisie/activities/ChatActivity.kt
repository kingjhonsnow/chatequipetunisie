package com.example.chatequipetunisie.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.adapters.ChatRecyclerAdapter
import com.example.chatequipetunisie.models.Message
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatActivity : AppCompatActivity() {

    lateinit var fabSendMessage: FloatingActionButton
    lateinit var editMessage: EditText
    lateinit var rvChatList: RecyclerView
    lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    lateinit var messages: MutableList<Message>
    private var discussionKey: String = "default_discussion"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fabSendMessage = findViewById(R.id.fabsendmessage)
        editMessage = findViewById(R.id.editMessage)
        rvChatList = findViewById(R.id.rvChatList)

        // Retrieve the friend's name and the account username from the Intent
        val friendName = intent.getStringExtra("friend") ?: "Unknown"
        val accountUsername = intent.getStringExtra("accountUsername") ?: "default_user"

        // Generate a unique key for this account and discussion
        discussionKey = "chat_${accountUsername}_with_${friendName}"

        supportActionBar?.title = friendName

        // Load messages for this specific account and discussion
        messages = loadMessages(discussionKey)

        // Set up RecyclerView
        chatRecyclerAdapter = ChatRecyclerAdapter()
        chatRecyclerAdapter.items = messages

        rvChatList.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatRecyclerAdapter
        }

        // Handle message sending
        fabSendMessage.setOnClickListener {
            val messageText = editMessage.text.toString()
            if (messageText.isNotEmpty()) {
                // Create a new message
                val newMessage = Message(
                    sender = accountUsername,
                    receiver = friendName,
                    text = messageText,
                    timestamp = System.currentTimeMillis(),
                    isReceived = false
                )
                // Add and display the new message
                messages.add(newMessage)
                chatRecyclerAdapter.notifyItemInserted(messages.size - 1)
                editMessage.text.clear()
                rvChatList.scrollToPosition(messages.size - 1)

                // Save messages for this specific account and discussion
                saveMessages(discussionKey, messages)
            }
        }
    }

    private fun saveMessages(key: String, messages: MutableList<Message>) {
        val sharedPreferences = getSharedPreferences("ChatPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(messages) // Convert list of messages to JSON
        editor.putString(key, json) // Save JSON string under the account-discussion-specific key
        editor.apply()
    }

    private fun loadMessages(key: String): MutableList<Message> {
        val sharedPreferences = getSharedPreferences("ChatPreferences", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(key, null) // Get JSON string for the specific key
        val type = object : TypeToken<MutableList<Message>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf() // Deserialize JSON or return empty list
    }
}
