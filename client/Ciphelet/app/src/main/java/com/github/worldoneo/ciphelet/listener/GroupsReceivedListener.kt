package com.github.worldoneo.ciphelet.listener

import com.github.worldoneo.ciphelet.adapter.ContactListAdapter
import com.github.worldoneo.ciphelet.connector.events.EventHandler
import com.github.worldoneo.ciphelet.connector.events.EventListener
import com.github.worldoneo.ciphelet.connector.events.GroupsReceivedEvent

class GroupsReceivedListener(var contactListAdapter: ContactListAdapter) : EventListener() {
    @EventHandler
    fun onGroupsReceived(event: GroupsReceivedEvent) {
        contactListAdapter.contacts = event.groups
    }
}