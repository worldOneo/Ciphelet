package com.github.worldoneo.ciphelet.connector

import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class ActionClient(serverURI: URI?) : WebSocketClient(serverURI) {
    private var actionConsumer: Consumer<GenericAction>? = null

    override fun onOpen(handshakedata: ServerHandshake) {}
    override fun onMessage(message: String) {
        println("received: $message")
        if (actionConsumer != null) {
            val genericAction = Connector.gson.fromJson(message, GenericAction::class.java)
            actionConsumer!!.accept(genericAction)
        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {}
    override fun onError(ex: Exception) {
        ex.printStackTrace()
        // if the error is fatal then onClose will be called additionally
    }

    fun setActionConsumer(actionConsumer: Consumer<GenericAction>) {
        this.actionConsumer = actionConsumer
    }
}