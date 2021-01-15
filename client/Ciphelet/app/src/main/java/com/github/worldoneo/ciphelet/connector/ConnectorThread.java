package com.github.worldoneo.ciphelet.connector;

import android.content.SharedPreferences;

import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.net.URI;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

public class ConnectorThread extends Thread {
    private final SharedPreferences sharedPreferences;
    private final URI u;

    public ConnectorThread(SharedPreferences sharedPreferences, URI u) {
        this.sharedPreferences = sharedPreferences;
        this.u = u;
    }


    @Override
    public void run() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Connector connector = new Connector(u);
        RegistrationService r = new RegistrationService(connector);
        if (!sharedPreferences.contains("privatekey")) {
            try {
                CipheletAPI cipheletAPI = r.register("test").get();
                editor.putString("privatekey", EncryptionUtility.EncodeKey(cipheletAPI.privateKey));
                System.out.println("Registered as: "+cipheletAPI.humanID);
                editor.putString("humanid", cipheletAPI.humanID);
                editor.apply();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Found private key: " + sharedPreferences.getString("privatekey", ""));
            PrivateKey privateKey = EncryptionUtility.DecodeKey(sharedPreferences.getString("privatekey", ""));
            CipheletAPI cipheletAPI = new CipheletAPI(sharedPreferences.getString("humanid", ""), connector, privateKey);
            cipheletAPI.login("test");
        }
    }
}
