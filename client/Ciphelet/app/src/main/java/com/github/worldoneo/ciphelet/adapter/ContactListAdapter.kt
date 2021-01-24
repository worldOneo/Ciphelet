package com.github.worldoneo.ciphelet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.worldoneo.ciphelet.R
import com.github.worldoneo.ciphelet.adapter.ContactListAdapter.Contact
import com.github.worldoneo.ciphelet.connector.FlakeHelper

class ContactListAdapter(var contacts: LongArray) : RecyclerView.Adapter<Contact>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Contact {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.contact_row, parent, false)
        return Contact(view)
    }

    override fun onBindViewHolder(holder: Contact, position: Int) {
        val flake = contacts[position]
        holder.id.text = flake.toString()
        holder.name.text = FlakeHelper.humanIDFromFlake(flake)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class Contact(itemView: View) : ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.chat_name)
        val id: TextView = itemView.findViewById(R.id.chat_id)
    }
}