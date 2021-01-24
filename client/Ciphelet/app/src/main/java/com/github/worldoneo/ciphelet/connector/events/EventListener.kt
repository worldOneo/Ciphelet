package com.github.worldoneo.ciphelet.connector.events

abstract class EventListener {
    var isRunning = true
        protected set

    fun cancel() {
        isRunning = false
    }
}
