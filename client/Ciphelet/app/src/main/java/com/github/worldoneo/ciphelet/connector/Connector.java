package com.github.worldoneo.ciphelet.connector;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connector {
    public static final Gson gson = new GsonBuilder().create();
    private final ActionClient Ws;
    private KeyPair keyPair;
    private Map<String, List<Consumer<GenericAction>>> actionListeners = new ConcurrentHashMap<>();
    private Map<String, List<Consumer<GenericAction>>> oneTimeListeners = new ConcurrentHashMap<>();


    public Connector(URI address) {
        Ws = new ActionClient(address);
        registerActions();
    }

    public boolean isConnected() {
        return Ws.isOpen();
    }

    public boolean Connect() {
        if (Ws.isOpen()) {
            return true;
        }
        try {
            System.out.println("Attempting to connect!");
            return Ws.connectBlocking();
        } catch (InterruptedException ex) {
            System.err.printf("Server down!, %s", ex.getMessage());
            return false;
        }
    }

    public void sendAction(GenericAction genericAction) {
        System.out.println("Sending: " + gson.toJson(genericAction));
        Ws.send(gson.toJson(genericAction));
    }

    public void registerActions() {
        Ws.setActionConsumer(new Consumer<GenericAction>() {
            @Override
            public void accept(GenericAction genericAction) {
                if (actionListeners.get(genericAction.action) != null) {
                    for (Consumer<GenericAction> actionConsumer : actionListeners.get(genericAction.action)) {
                        actionConsumer.accept(genericAction);
                    }
                }
                if (oneTimeListeners.get(genericAction.action) != null) {
                    for (Consumer<GenericAction> actionConsumer : oneTimeListeners.get(genericAction.action)) {
                        actionConsumer.accept(genericAction);
                    }
                    oneTimeListeners.remove(genericAction.action);
                }
            }
        });
    }


    public void actionHook(String action, Consumer<GenericAction> actionConsumer) {
        if (!actionListeners.containsKey(action)) {
            actionListeners.put(action, new ArrayList<Consumer<GenericAction>>());
        }
        actionListeners.get(action).add(actionConsumer);
    }

    public void once(String action, Consumer<GenericAction> actionConsumer) {
        if (!oneTimeListeners.containsKey(action)) {
            oneTimeListeners.put(action, new ArrayList<Consumer<GenericAction>>());
        }
        oneTimeListeners.get(action).add(actionConsumer);
    }
}
