package com.github.worldoneo.ciphelet.connector;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.action.RegisterAction;
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.iwebpp.crypto.TweetNaclFast;

public class RegistrationService {
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
        final TweetNaclFast.Box.KeyPair keyPair = EncryptionUtility.GenerateKeypair();
        GenericAction genericAction = new GenericAction("register");
        RegisterAction rAction = new RegisterAction();
        rAction.password = password;
        byte[] pKey = keyPair.getPublicKey();
        rAction.key = EncryptionUtility.encodeKey(pKey);
        genericAction.registerAction = rAction;
        final byte[] privateKey = keyPair.getSecretKey();
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
