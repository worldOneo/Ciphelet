package com.github.worldoneo.ciphelet.connector;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.RegisterAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistrationService {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Connector connector;

    public RegistrationService(Connector connector) {
        this.connector = connector;
    }

    public CipheletAPI register(final String password) {
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
        try {
            final PrivateKey privateKey = keyPair.getPrivate();
            connector.awaitAction(new ChallengeHandler(privateKey, connector));
            connector.awaitAction(new Consumer<GenericAction>() {
                @Override
                public void accept(GenericAction genericAction) {
                    cipheletAPI[0] = new CipheletAPI(genericAction.registerAction.humanid, connector, privateKey);
                }
            });
            return cipheletAPI[0];
        } catch (InterruptedException e) {
            return null;
        }
    }
}
