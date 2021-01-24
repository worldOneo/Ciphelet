package com.github.worldoneo.ciphelet.connector.events

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class RegisteredListener(private val instance: EventListener, val method: Method) {
    fun getInstance(): Any {
        return instance
    }

    val isRunning: Boolean
        get() = instance.isRunning

    fun fireEvent(chatExEvent: Event?) {
        method.isAccessible = true
        try {
            method.invoke(getInstance(), chatExEvent)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

}