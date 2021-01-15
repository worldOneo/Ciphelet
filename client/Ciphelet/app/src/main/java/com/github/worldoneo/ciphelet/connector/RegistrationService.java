package com.github.worldoneo.ciphelet.connector;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.RegisterAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.KeyPair;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegistrationService {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Connector connector;

    public RegistrationService(Connector connector) {
        this.connector = connector;
    }

    public Future<CipheletAPI> register(final String password) {
        return executorService.submit(new Callable<CipheletAPI>() {
            @Override
            public CipheletAPI call() {
                return register0(password);
            }
        });
    }

    private CipheletAPI register0(String password) {
        connector.Connect();
        System.out.println("Register0");
        final CipheletAPI[] cipheletAPI = new CipheletAPI[1];
        final KeyPair keyPair = EncryptionUtility.GenerateKeypair();
        GenericAction genericAction = new GenericAction("register");
        RegisterAction rAction = new RegisterAction();
        rAction.password = password;
        byte[] enced = keyPair.getPublic().getEncoded();
        rAction.key = new String(Base64.encode(enced, Base64.DEFAULT)).replace("\n", "");
        genericAction.registerAction = rAction;
        connector.sendAction(genericAction);
        connector.actionHook(GenericAction.ChallengeAction, new ChallengeHandler(keyPair, connector));
        connector.actionHook(GenericAction.RegisterAction, new Consumer<GenericAction>() {
            @Override
            public void accept(GenericAction genericAction) {
                cipheletAPI[0] = new CipheletAPI(genericAction.registerAction.humanID, connector, keyPair);
            }
        });
        while (cipheletAPI[0] == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cipheletAPI[0];
    }
}
