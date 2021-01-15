package com.github.worldoneo.ciphelet.connector.api;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class ChallengeHandler implements Consumer<GenericAction> {
    private final KeyPair keyPair;
    private final Connector connector;
    private Runnable callback;

    public ChallengeHandler(KeyPair keyPair, Connector connector) {
        this.keyPair = keyPair;
        this.connector = connector;
    }

    public ChallengeHandler(KeyPair keyPair, Connector connector, Runnable callback) {
        this(keyPair, connector);
        this.callback = callback;
    }

    @Override
    public void accept(GenericAction genericAction) {
        System.out.println("Challenge handled");
        try {
            GenericAction response = new GenericAction(GenericAction.ChallengeAction);
            ChallengeAction challengeAction = new ChallengeAction();
            challengeAction.token = new String(EncryptionUtility.decrypt(
                    Base64.decode(genericAction.challengeAction.token.getBytes(), Base64.DEFAULT),
                    keyPair.getPrivate()));
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
