package com.github.worldoneo.ciphelet.connector;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.LoginAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;
import com.github.worldoneo.ciphelet.connector.hooks.ChatfetchHook;

import java.security.PrivateKey;
import java.security.PublicKey;


public class CipheletAPI {
    public final PrivateKey privateKey;
    public final String humanID;
    private Connector connector;
    private boolean loggedIn = false;
    private boolean hooked = false;
    private long[] chatids;
    private Consumer<CipheletAPI> onLogin;

    public CipheletAPI(String humanID, Connector server, PrivateKey privateKey) {
        this.humanID = humanID;
        this.privateKey = privateKey;
        this.connector = server;
        connector.actionHook(GenericAction.ChallengeAction, new ChallengeHandler(privateKey, connector));
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
        final CipheletAPI api = this;
        connector.actionHook(GenericAction.ChallengeAction, new ChallengeHandler(privateKey, connector, new Runnable() {
            @Override
            public void run() {
                challengeDone();
                onLogin.accept(api);
            }
        }));
    }

    private void challengeDone() {
        System.out.println("Logged in");
        loggedIn = true;
    }

    public void hook() {
        if (hooked) return;
        hooked = true;
        connector.actionHook(GenericAction.ChatfetchAction, new ChatfetchHook(this));
    }

    public void setChatids(long[] chatids) {
        System.out.println("Recieved chat ids! : " + chatids);
        this.chatids = chatids;
    }

    public void onLogin(Consumer<CipheletAPI> onLogin) {
        this.onLogin = onLogin;
    }
}
