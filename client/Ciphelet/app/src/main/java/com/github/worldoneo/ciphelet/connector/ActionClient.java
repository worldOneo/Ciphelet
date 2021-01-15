package com.github.worldoneo.ciphelet.connector;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class ActionClient extends WebSocketClient {

    private Consumer<GenericAction> actionConsumer;

    public ActionClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public ActionClient(URI serverURI) {
        super(serverURI);
    }

    public ActionClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
        if (actionConsumer != null) {
            GenericAction genericAction = Connector.gson.fromJson(message, GenericAction.class);
            actionConsumer.accept(genericAction);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public void setActionConsumer(Consumer<GenericAction> actionConsumer) {
        this.actionConsumer = actionConsumer;
    }
}
