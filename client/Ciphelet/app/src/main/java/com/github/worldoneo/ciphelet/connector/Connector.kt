package com.github.worldoneo.ciphelet.connector

import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.google.gson.GsonBuilder
import java.net.URI
import java.security.KeyPair
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Connector(address: URI?) {
    private val Ws: ActionClient = ActionClient(address)
    private val keyPair: KeyPair? = null
    private val actionListeners: MutableMap<String, MutableList<Consumer<GenericAction>>> = ConcurrentHashMap()
    private val oneTimeListeners: MutableMap<String, MutableList<Consumer<GenericAction>>> = ConcurrentHashMap()
    val isConnected: Boolean
        get() = Ws.isOpen

    fun Connect(): Boolean {
        return if (Ws.isOpen) {
            true
        } else try {
            println("Attempting to connect!")
            Ws.connectBlocking()
        } catch (ex: InterruptedException) {
            System.err.printf("Server down!, %s", ex.message)
            false
        }
    }

    fun sendAction(genericAction: GenericAction?) {
        println("Sending: " + gson.toJson(genericAction))
        Ws.send(gson.toJson(genericAction))
    }

    fun registerActions() {
        Ws.setActionConsumer(Consumer { genericAction ->
            if (actionListeners[genericAction.action] != null) {
                for (actionConsumer in actionListeners[genericAction.action]!!) {
                    actionConsumer.accept(genericAction)
                }
            }
            if (oneTimeListeners[genericAction.action] != null) {
                for (actionConsumer in oneTimeListeners[genericAction.action]!!) {
                    actionConsumer.accept(genericAction)
                }
                oneTimeListeners.remove(genericAction.action)
            }
        })
    }

    fun actionHook(action: String, actionConsumer: Consumer<GenericAction>) {
        if (!actionListeners.containsKey(action)) {
            actionListeners[action] = ArrayList()
        }
        actionListeners[action]!!.add(actionConsumer)
    }

    fun once(action: String, actionConsumer: Consumer<GenericAction>) {
        if (!oneTimeListeners.containsKey(action)) {
            oneTimeListeners[action] = ArrayList()
        }
        oneTimeListeners[action]!!.add(actionConsumer)
    }

    init {
        registerActions()
    }

    companion object {
        val gson = GsonBuilder().create()
    }
}