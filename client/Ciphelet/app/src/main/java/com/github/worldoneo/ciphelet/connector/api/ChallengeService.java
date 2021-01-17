package com.github.worldoneo.ciphelet.connector.api;

import android.util.Base64;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.GeneralSecurityException;

public class ChallengeService extends Service<Void> {
    public ChallengeService(Connector connector, CipheletAPI cipheletAPI) {
        super(connector, cipheletAPI, GenericAction.Action.CHALLENGE);
    }

    @Override
    public void receivedAction(GenericAction gAction) {
        ChallengeAction action = gAction.challengeAction;
        System.out.println("Challenge handled");
        try {
            GenericAction response = new GenericAction(GenericAction.CHALLENGE_ACTION);
            ChallengeAction challengeAction = new ChallengeAction();
            challengeAction.token = new String(EncryptionUtility.decryptRSA(
                    Base64.decode(action.token.getBytes(), Base64.DEFAULT),
                    cipheletAPI.privateKey));
            response.challengeAction = challengeAction;
            connector.sendAction(response);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
