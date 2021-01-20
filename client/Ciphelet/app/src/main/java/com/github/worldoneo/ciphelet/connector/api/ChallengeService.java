package com.github.worldoneo.ciphelet.connector.api;

import android.util.Base64;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.github.worldoneo.ciphelet.connector.events.ChallengeSuccessEvent;
import com.github.worldoneo.ciphelet.connector.events.EventManager;

public class ChallengeService extends Service {
    public ChallengeService(Connector connector, CipheletAPI cipheletAPI) {
        super(connector, cipheletAPI, GenericAction.Action.CHALLENGE);
    }

    @Override
    public void receivedAction(GenericAction gAction) {
        ChallengeAction action = gAction.challengeAction;
        System.out.println("Challenge handled");
        GenericAction response = new GenericAction(GenericAction.CHALLENGE_ACTION);
        ChallengeAction challengeAction = new ChallengeAction();
        byte[] bytes = EncryptionUtility.decryptNaCL(
                Base64.decode(action.token.getBytes(), Base64.DEFAULT),
                EncryptionUtility.decodeKey(action.publickey), cipheletAPI.privateKey);
        challengeAction.token = new String(bytes);
        response.challengeAction = challengeAction;
        connector.sendAction(response);
        EventManager.getInstance().handleEvent(new ChallengeSuccessEvent(challengeAction));
    }
}
