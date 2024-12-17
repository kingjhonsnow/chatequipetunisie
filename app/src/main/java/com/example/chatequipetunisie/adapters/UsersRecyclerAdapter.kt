package com.example.chatequipetunisie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatequipetunisie.R
import com.example.chatequipetunisie.models.User
import java.util.*

class UsersRecyclerAdapter : RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder>(), Filterable {

    var items: MutableList<User> = mutableListOf()
        set(value) {
            field = value
            filteredItems = value // Update filtered items too
            notifyDataSetChanged()
        }

    private var filteredItems: MutableList<User> = mutableListOf()

    init {
        filteredItems = items
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""
                val resultList = if (charSearch.isEmpty()) {
                    items // No filter, show all items
                } else {
                    items.filter {
                        it.fullName.lowercase(Locale.getDefault()).contains(charSearch) ||
                                it.email.lowercase(Locale.getDefault()).contains(charSearch)
                    }.toMutableList()
                }

                val filterResults = FilterResults()
                filterResults.values = resultList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as? MutableList<User> ?: mutableListOf()
                notifyDataSetChanged()  // Notify adapter to update the view
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = filteredItems[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = filteredItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvShortName: TextView = itemView.findViewById(R.id.tvShortName)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)

        fun bind(user: User) {
            tvShortName.text = user.fullName[0].toString().uppercase()
            tvName.text = user.fullName
        }
    }
}
