package com.github.worldoneo.ciphelet.connector;

import com.github.worldoneo.ciphelet.MainActivity;
import com.github.worldoneo.ciphelet.R;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.github.worldoneo.ciphelet.storage.SecureStorage;

import java.net.URI;

import lombok.SneakyThrows;

public class ConnectorThread extends Thread {
    private final String password;
    private final URI u;
    private final SecureStorage secureStorage;


    public ConnectorThread(String password, URI u, SecureStorage secureStorage) {
        this.password = password;
        this.u = u;
        this.secureStorage = secureStorage;
    }


    @SneakyThrows
    @Override
    public void run() {
        final URI uri = new URI(MainActivity.getInstance().getStringsxml(R.string.server));
        CipheletAPI cipheletAPI = new CipheletAPI(
                secureStorage.get("humanid", String.class),
                new Connector(uri),
                EncryptionUtility.DecodeKey(secureStorage.get("privatekey", String.class)));
        cipheletAPI.login(password);
        MainActivity.getInstance().loggedIn(cipheletAPI);
    }
}
