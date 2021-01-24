package com.github.worldoneo.ciphelet.connector.api

import com.github.worldoneo.ciphelet.connector.CipheletAPI
import com.github.worldoneo.ciphelet.connector.Connector
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.github.worldoneo.ciphelet.connector.events.EventManager
import com.github.worldoneo.ciphelet.connector.events.EventManager.handleEvent
import com.github.worldoneo.ciphelet.connector.events.GroupsReceivedEvent
import java.util.*

class GroupfetchService(connector: Connector?, cipheletAPI: CipheletAPI) : Service(connector, cipheletAPI, GenericAction.Action.GROUPFETCH) {
    override fun receivedAction(action: GenericAction) {
        handleEvent(GroupsReceivedEvent(action.groupfetchAction!!.chatid))
    }

    override fun run() {
        val genericAction = GenericAction(GenericAction.Action.GROUPFETCH.request)
        connector!!.sendAction(genericAction)
    }
}