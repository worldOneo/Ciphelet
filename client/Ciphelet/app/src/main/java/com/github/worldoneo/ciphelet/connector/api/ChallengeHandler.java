package com.github.worldoneo.ciphelet.connector.api;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;

public class ChallengeHandler implements Consumer<GenericAction> {
    private final PrivateKey privateKey;
    private final Connector connector;
    private Runnable callback;

    public ChallengeHandler(PrivateKey privateKey, Connector connector) {
        this.privateKey = privateKey;
        this.connector = connector;
    }

    public ChallengeHandler(PrivateKey privateKey, Connector connector, Runnable callback) {
        this(privateKey, connector);
        this.callback = callback;
    }

    @Override
    public void accept(GenericAction genericAction) {
        if (!genericAction.action.equals(GenericAction.ChallengeAction)) {
            System.out.println("Action listener recieved wrong action");
            return;
        }
        System.out.println("Challenge handled");
        try {
            GenericAction response = new GenericAction(GenericAction.ChallengeAction);
            ChallengeAction challengeAction = new ChallengeAction();
            challengeAction.token = new String(EncryptionUtility.decrypt(
                    Base64.decode(genericAction.challengeAction.token.getBytes(), Base64.DEFAULT),
                    privateKey));
            response.challengeAction = challengeAction;
            connector.sendAction(response);
            if (callback != null) {
                callback.run();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
