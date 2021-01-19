package com.github.worldoneo.ciphelet.connector.api;

import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

public class ChallengeHandler implements Consumer<GenericAction> {
    private final byte[] privateKey;
    private final Connector connector;
    private Runnable callback;

    public ChallengeHandler(byte[] privateKey, Connector connector) {
        this.privateKey = privateKey;
        this.connector = connector;
    }

    public ChallengeHandler(byte[] privateKey, Connector connector, Runnable callback) {
        this(privateKey, connector);
        this.callback = callback;
    }

    @Override
    public void accept(GenericAction genericAction) {
        if (!genericAction.action.equals(GenericAction.CHALLENGE_ACTION)) {
            System.out.println("Action listener recieved wrong action");
            return;
        }
        ChallengeAction requestedAction = genericAction.challengeAction;
        System.out.println("Challenge handled with pkey: "+ requestedAction.publickey);
        GenericAction response = new GenericAction(GenericAction.CHALLENGE_ACTION);
        ChallengeAction challengeAction = new ChallengeAction();
        byte[] decrypted = EncryptionUtility.decryptNaCL(
                Base64.decode(requestedAction.token.getBytes(), Base64.DEFAULT),
                EncryptionUtility.decodeKey(requestedAction.publickey), privateKey);
        challengeAction.token = new String(decrypted);
        response.challengeAction = challengeAction;
        connector.sendAction(response);
        if (callback != null) {
            callback.run();
        }
    }
}
