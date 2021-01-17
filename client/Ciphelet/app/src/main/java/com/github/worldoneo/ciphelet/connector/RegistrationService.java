package com.github.worldoneo.ciphelet.connector;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.RegisterAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;
import com.github.worldoneo.ciphelet.connector.api.ChallengeService;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistrationService {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Connector connector;
    private Consumer<CipheletAPI> onRegister;

    public RegistrationService(Connector connector) {
        this.connector = connector;
    }

    public void onRegister(Consumer<CipheletAPI> register) {
        this.onRegister = register;
    }

    public void register(final String password) {
        connector.Connect();
        System.out.println("Register0");
        final KeyPair keyPair = EncryptionUtility.GenerateKeypair();
        GenericAction genericAction = new GenericAction("register");
        RegisterAction rAction = new RegisterAction();
        rAction.password = password;
        byte[] enced = keyPair.getPublic().getEncoded();
        rAction.key = new String(Base64.encode(enced, Base64.DEFAULT)).replace("\n", "");
        genericAction.registerAction = rAction;
        final PrivateKey privateKey = keyPair.getPrivate();
        connector.once(GenericAction.Action.REGISTER.response, new Consumer<GenericAction>() {
            @Override
            public void accept(GenericAction genericAction) {
                onRegister.accept(new CipheletAPI(genericAction.registerAction.humanid, connector, privateKey));
            }
        });

        connector.once(GenericAction.Action.CHALLENGE.response, new ChallengeHandler(privateKey, connector));
        connector.sendAction(genericAction);
    }
}
