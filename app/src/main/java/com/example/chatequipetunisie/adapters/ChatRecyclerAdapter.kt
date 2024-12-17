package com.example.chatequipetunisie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRecyclerAdapter : RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {

    var items: MutableList<Message> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // Return different layout resources for sent and received messages
    override fun getItemViewType(position: Int): Int {
        return when (items[position].isReceived) {
            true -> R.layout.item_chat_left  // For received messages (left-aligned)
            false -> R.layout.item_chat_right // For sent messages (right-aligned)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = items[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = items.size

    // ViewHolder class to hold item views
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvmsg)
        private val tvHour: TextView = itemView.findViewById(R.id.tvhour)

        fun bind(message: Message) {
            tvMessage.text = message.text
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvHour.text = sdf.format(Date(message.timestamp)) // Format the timestamp
        }
    }
}
