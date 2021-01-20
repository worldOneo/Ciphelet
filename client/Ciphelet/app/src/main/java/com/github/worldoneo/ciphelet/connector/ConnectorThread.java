package com.github.worldoneo.ciphelet.connector;

import android.content.SharedPreferences;
import android.util.Base64;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.MainActivity;
import com.github.worldoneo.ciphelet.R;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.github.worldoneo.ciphelet.connector.events.EventManager;
import com.github.worldoneo.ciphelet.connector.events.LoginSuccessEvent;
import com.github.worldoneo.ciphelet.storage.SecureStorage;

import java.net.URI;
import java.net.URISyntaxException;

public class ConnectorThread extends Thread {
    private final String password;
    private final URI u;
    private final SharedPreferences preferences;
    private SecureStorage secureStorage;


    public ConnectorThread(String password, URI u, SharedPreferences preferences) {
        this.password = password;
        this.u = u;
        this.preferences = preferences;
    }

    @Override
    public void run() {
        this.secureStorage = new SecureStorage(preferences, EncryptionUtility.getKeyFromPassword(password, SecureStorage.getSalt(preferences)));
        System.out.println("Going async!");
        URI uri = null;
        try {
            uri = new URI(MainActivity.getInstance().getStringsxml(R.string.server));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        CipheletAPI cipheletAPI = new CipheletAPI(
                secureStorage.get("userid", Long.class),
                new Connector(uri),
                EncryptionUtility.decodeKey(secureStorage.get("privatekey", String.class)));
        cipheletAPI.onLogin(new Consumer<CipheletAPI>() {
            @Override
            public void accept(CipheletAPI cipheletAPI) {
                EventManager.getInstance().handleEvent(new LoginSuccessEvent(cipheletAPI));
            }
        });
        cipheletAPI.login(password);
    }
}
