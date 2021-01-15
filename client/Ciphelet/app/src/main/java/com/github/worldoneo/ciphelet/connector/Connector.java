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

public class Connector {
    public static final Gson gson = new GsonBuilder().create();
    private final ActionClient Ws;
    private KeyPair keyPair;

    public Connector(URI address) {
        Ws = new ActionClient(address);
    }

    public void RegisterHandShake() {
        try {
            System.out.println("Attempting to connect!");
            Ws.connectBlocking();
            System.out.println("Successfully connected");
        } catch (InterruptedException ignore) {
            System.err.println("Server down!");
            return;
        }
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
        keyPair = EncryptionUtility.GenerateKeypair();
        GenericAction genericAction = new GenericAction("register");
        RegisterAction rAction = new RegisterAction();
        rAction.password = "1234";
        byte[] enced = keyPair.getPublic().getEncoded();
        rAction.key = new String(Base64.encode(enced, Base64.DEFAULT)).replace("\n", "");
        genericAction.registerAction = rAction;
        Ws.send(gson.toJson(genericAction));
    }

    private void AcceptAction(GenericAction genericAction) throws GeneralSecurityException {
        System.out.printf("Action recieved:%s\n", genericAction.action);
        if (genericAction.action.equals("challenge")) {
            GenericAction response = new GenericAction("challenge");
            ChallengeAction challengeAction = new ChallengeAction();
            challengeAction.token = new String(EncryptionUtility.decrypt(
                    Base64.decode(genericAction.challengeAction.token.getBytes(), Base64.DEFAULT),
                    keyPair.getPrivate())).intern();
            response.challengeAction = challengeAction;
            Ws.send(gson.toJson(response));
        }
    }
}
