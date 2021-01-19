package com.github.worldoneo.ciphelet.connector.events;

public abstract class EventListener {
    protected boolean running = true;

    public boolean isRunning() {
        return running;
    }

    void cancel() {
        this.running = false;
    }
}
