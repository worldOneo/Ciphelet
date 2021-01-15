package com.github.worldoneo.ciphelet.connector;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.RegisterAction;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class Connector {
    public static final Gson gson = new GsonBuilder().create();
    private final ActionClient Ws;
    private KeyPair keyPair;
    private Map<String, Queue<Consumer<GenericAction>>> waitingListeners = new ConcurrentHashMap<>();

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
        System.out.println("Sending: "+gson.toJson(genericAction));
        Ws.send(gson.toJson(genericAction));
    }

    public void actionHook(String actionType, Consumer<GenericAction> consumer) {
        Queue<Consumer<GenericAction>> consumerQueue = waitingListeners.get(actionType);
        if (consumerQueue == null) {
            waitingListeners.put(actionType, new LinkedList<Consumer<GenericAction>>());
        }
        waitingListeners.get(actionType).add(consumer);
    }

    public void registerActions() {

        Ws.setActionConsumer(new Consumer<GenericAction>() {
            @Override
            public void accept(GenericAction genericAction) {
                try {
                    AcceptAction(genericAction);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void AcceptAction(GenericAction genericAction) throws GeneralSecurityException {
        Queue<Consumer<GenericAction>> cQ = waitingListeners.get(genericAction.action);
        if (cQ != null) {
            Consumer<GenericAction> consumer = cQ.poll();
            if (consumer != null) {
                consumer.accept(genericAction);
            }
        }
    }
}
