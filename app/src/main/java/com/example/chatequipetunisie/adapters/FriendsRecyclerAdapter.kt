package com.example.chatequipetunisie.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.activities.ChatActivity
import com.example.chatequipetunisie.models.Friend
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FriendsRecyclerAdapter : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {

    var items: MutableList<Friend> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = items[position]
        holder.bind(friend)
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFriend: ShapeableImageView = itemView.findViewById(R.id.ivfriend)
        private val tvName: TextView = itemView.findViewById(R.id.tvname)
        private val tvLastMsg: TextView = itemView.findViewById(R.id.tvlastmsg)
        private val tvHour: TextView = itemView.findViewById(R.id.tvhour)

        fun bind(friend: Friend) {
            tvName.text = friend.name
            tvLastMsg.text = friend.lastM

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvHour.text = sdf.format(Date(friend.timestamp))
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("friend", friend.name)  // Ensure you're passing the friend's name correctly
                itemView.context.startActivity(intent)
            }

        }
    }
}
