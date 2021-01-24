package com.github.worldoneo.ciphelet.connector.api

import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.connector.CipheletAPI
import com.github.worldoneo.ciphelet.connector.Connector
import com.github.worldoneo.ciphelet.connector.action.GenericAction

abstract class Service(protected val connector: Connector?, protected val cipheletAPI: CipheletAPI, protected val action: GenericAction.Action) : Consumer<GenericAction>, Runnable {
    override fun accept(t: GenericAction) {
        receivedAction(t)
    }

    override fun run() {}
    open fun receivedAction(action: GenericAction) {}
    val recievingPacket: String
        get() = action.response

}