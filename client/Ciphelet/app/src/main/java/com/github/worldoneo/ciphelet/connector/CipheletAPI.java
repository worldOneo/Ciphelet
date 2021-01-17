package com.github.worldoneo.ciphelet.connector;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.LoginAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeService;
import com.github.worldoneo.ciphelet.connector.api.GroupfetchService;
import com.github.worldoneo.ciphelet.connector.api.Service;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CipheletAPI {
    public final PrivateKey privateKey;
    public final String humanID;
    private final Map<String, Service<?>> services = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private Connector connector;
    private boolean loggedIn = false;
    private Consumer<CipheletAPI> onLogin;

    public CipheletAPI(String humanID, Connector server, PrivateKey privateKey) {
        this.humanID = humanID;
        this.privateKey = privateKey;
        this.connector = server;

        registerService(new ChallengeService(connector, this));
        registerService(new GroupfetchService(connector, this));
    }

    private void fetch() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                services.get(GenericAction.Action.GROUPFETCH.response).run();
            }
        });
    }

    public void login(String password) {
        if (!connector.Connect()) {
            System.err.println("Couldn't connect to server!");
            return;
        }
        GenericAction genericAction = new GenericAction(GenericAction.LOGIN_ACTION);
        LoginAction loginAction = new LoginAction();
        System.out.println("Logging in as: " + this.humanID);
        loginAction.humanid = this.humanID;
        loginAction.password = password;
        genericAction.loginAction = loginAction;
        connector.sendAction(genericAction);
        final CipheletAPI api = this;
        connector.once(GenericAction.CHALLENGE_ACTION, new Consumer<GenericAction>() {
            @Override
            public void accept(GenericAction genericAction) {
                challengeDone();
                onLogin.accept(api);
            }
        });
    }

    private void challengeDone() {
        System.out.println("Logged in");
        fetch();
    }

    public void onLogin(Consumer<CipheletAPI> onLogin) {
        this.onLogin = onLogin;
    }

    public void registerService(Service<?> service) {
        services.put(service.getRecievingPacket(), service);
        connector.actionHook(service.getRecievingPacket(), service);
    }
}
