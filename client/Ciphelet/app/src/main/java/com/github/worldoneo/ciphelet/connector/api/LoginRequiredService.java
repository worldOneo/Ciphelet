package com.github.worldoneo.ciphelet.connector.api;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.events.EventManager;
import com.github.worldoneo.ciphelet.connector.events.LoginRequiredEvent;

public class LoginRequiredService extends Service {
    public LoginRequiredService(Connector connector, CipheletAPI cipheletAPI) {
        super(connector, cipheletAPI, GenericAction.Action.LOGIN_REQUIRED);
    }

    @Override
    public void receivedAction(GenericAction action) {
        EventManager.getInstance().handleEvent(new LoginRequiredEvent());
    }
}
