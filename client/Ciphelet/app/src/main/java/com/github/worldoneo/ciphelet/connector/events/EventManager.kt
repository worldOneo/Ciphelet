package com.github.worldoneo.ciphelet.connector.events

import com.github.worldoneo.ciphelet.MainActivity
import java.util.*

object EventManager {
    private val eventClassMap: MutableMap<Class<out Event>, HashSet<RegisteredListener>?> = mutableMapOf()
    fun registerEvent(listener: EventListener) {
        for (method in listener.javaClass.declaredMethods) {
            method.getAnnotation(EventHandler::class.java) ?: continue
            val checkClass = method.parameterTypes[0]
            if (method.parameterTypes.size != 1 || !Event::class.java.isAssignableFrom(checkClass)) {
                println("Unable to register $listener mismatching parameters")
                continue
            }
            if (checkClass == null) {
                println("Unable to register $listener null check failed")
                continue
            }
            val eventClass = checkClass.asSubclass(Event::class.java)
            if (!eventClassMap.containsKey(eventClass)) {
                eventClassMap[eventClass] = HashSet()
            }
            eventClassMap[eventClass]!!.add(RegisteredListener(listener, method))
        }
    }

    fun handleEvent(event: Event) {
        MainActivity.instance!!.runOnUiThread(Runnable {
            if (!eventClassMap.containsKey(event::class.java)) return@Runnable
            val registeredListeners = eventClassMap[event.javaClass]
            for (registeredListener in registeredListeners!!) {
                if (registeredListener.isRunning) {
                    registeredListener.fireEvent(event)
                } else {
                    registeredListeners.remove(registeredListener)
                }
            }
        })
    }
}