package com.github.worldoneo.ciphelet.listener

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.worldoneo.ciphelet.MainActivity
import com.github.worldoneo.ciphelet.R
import com.github.worldoneo.ciphelet.adapter.ContactListAdapter
import com.github.worldoneo.ciphelet.connector.FlakeHelper.humanIDFromFlake
import com.github.worldoneo.ciphelet.connector.events.EventHandler
import com.github.worldoneo.ciphelet.connector.events.EventListener
import com.github.worldoneo.ciphelet.connector.events.EventManager
import com.github.worldoneo.ciphelet.connector.events.LoginSuccessEvent

class LoggedInListener : EventListener() {

    @EventHandler
    fun onLogin(event: LoginSuccessEvent) {
        val instance = MainActivity.instance!!
        instance.setContentView(R.layout.activity_welcome)
        (instance.findViewById<View>(R.id.welcomeText) as TextView).text = String.format(instance.getStringsxml(R.string.welcome),
                humanIDFromFlake(event.cipheletAPI.userid))

        val recyclerView = instance.findViewById<RecyclerView>(R.id.chatList)
        recyclerView.layoutManager = LinearLayoutManager(instance)
        val contactListAdapter = ContactListAdapter(longArrayOf())
        recyclerView.adapter = contactListAdapter
        EventManager.registerEvent(GroupsReceivedListener(contactListAdapter))
    }
}