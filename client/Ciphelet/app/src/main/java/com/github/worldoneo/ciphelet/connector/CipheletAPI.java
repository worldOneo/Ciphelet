package com.github.worldoneo.ciphelet.connector;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.LoginAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;

import java.net.URI;
import java.security.KeyPair;

public class CipheletAPI {
    private final String humanID;
    private final KeyPair keyPair;
    private long userID;
    private Connector connector;
    private boolean loggedIn = false;

    public CipheletAPI(String humanID, Connector server, KeyPair keyPair) {
        this.humanID = humanID;
        this.keyPair = keyPair;
        this.connector = server;
    }

    public void login(String password) {
        if (!connector.Connect()) {
            return;
        }
        GenericAction genericAction = new GenericAction(GenericAction.LoginAction);
        LoginAction loginAction = new LoginAction();
        loginAction.humanId = this.humanID;
        loginAction.password = password;
        genericAction.loginAction = loginAction;
        connector.sendAction(genericAction);
        connector.actionHook(GenericAction.ChallengeAction, new ChallengeHandler(keyPair, connector, new Runnable() {
            @Override
            public void run() {
                challengeDone();
            }
        }));
    }

    public void challengeDone() {
        loggedIn = true;
    }
}
