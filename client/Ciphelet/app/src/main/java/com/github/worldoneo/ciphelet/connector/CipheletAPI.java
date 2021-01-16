package com.github.worldoneo.ciphelet.connector;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.LoginAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;

import java.security.PrivateKey;

public class CipheletAPI {
    public final PrivateKey privateKey;
    public final String humanID;
    private Connector connector;
    private boolean loggedIn = false;

    public CipheletAPI(String humanID, Connector server, PrivateKey privateKey) {
        this.humanID = humanID;
        this.privateKey = privateKey;
        this.connector = server;
    }

    public void login(String password) {
        if (!connector.Connect()) {
            System.err.println("Couldn't connect to server!");
            return;
        }
        GenericAction genericAction = new GenericAction(GenericAction.LoginAction);
        LoginAction loginAction = new LoginAction();
        System.out.println("Logging in as: " + this.humanID);
        loginAction.humanid = this.humanID;
        loginAction.password = password;
        genericAction.loginAction = loginAction;
        connector.sendAction(genericAction);
        try {
            connector.awaitAction(new ChallengeHandler(privateKey, connector, new Runnable() {
                @Override
                public void run() {
                    challengeDone();
                }
            }));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void challengeDone() {
        System.out.println("Logged in");
        loggedIn = true;
    }
}
