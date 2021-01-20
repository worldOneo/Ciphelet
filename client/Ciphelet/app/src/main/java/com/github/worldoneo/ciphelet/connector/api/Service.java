package com.github.worldoneo.ciphelet.connector.api;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;

public abstract class Service implements Consumer<GenericAction>, Runnable {
    protected final Connector connector;
    protected final CipheletAPI cipheletAPI;
    protected final GenericAction.Action action;

    public Service(Connector connector, CipheletAPI cipheletAPI, GenericAction.Action action) {
        this.connector = connector;
        this.cipheletAPI = cipheletAPI;
        this.action = action;
    }

    @Override
    public void accept(GenericAction t) {
        receivedAction(t);
    }

    @Override
    public void run() {
    }

    public void receivedAction(GenericAction action) {
    }

    public String getRecievingPacket() {
        return action.response;
    }
}
