package com.github.worldoneo.ciphelet.connector.api

import com.github.worldoneo.ciphelet.connector.CipheletAPI
import com.github.worldoneo.ciphelet.connector.Connector
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.github.worldoneo.ciphelet.connector.events.EventManager.handleEvent
import com.github.worldoneo.ciphelet.connector.events.LoginRequiredEvent

class LoginRequiredService(connector: Connector?, cipheletAPI: CipheletAPI) : Service(connector, cipheletAPI, GenericAction.Action.LOGIN_REQUIRED) {
    override fun receivedAction(action: GenericAction) {
        handleEvent(LoginRequiredEvent())
    }
}