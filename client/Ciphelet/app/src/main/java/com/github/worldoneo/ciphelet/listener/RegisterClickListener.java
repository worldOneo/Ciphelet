package com.github.worldoneo.ciphelet.listener;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import com.github.worldoneo.ciphelet.MainActivity;
import com.github.worldoneo.ciphelet.R;
import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.RegistrationService;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.github.worldoneo.ciphelet.storage.SecureStorage;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class RegisterClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        final MainActivity instance = MainActivity.getInstance();
        View registerInput = instance.findViewById(R.id.registerPasswordInput);
        final String password = ((EditText) registerInput).getText().toString();
        if (password.equals("")) {
            registerInput.setBackgroundColor(0xFFFFA3A3);
            return;
        }
        v.setEnabled(false);

        try {
            final SharedPreferences preferences = instance.getPreferences();
            final URI uri = new URI(instance.getStringsxml(R.string.server));
            final SecureStorage secureStorage = new SecureStorage(preferences, EncryptionUtility.getKeyFromPassword(password, SecureStorage.getSalt(preferences)));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connector connector = new Connector(uri);
                    RegistrationService r = new RegistrationService(connector);
                    CipheletAPI cipheletAPI = r.register(password);
                    secureStorage.store("privatekey", EncryptionUtility.EncodeKey(cipheletAPI.privateKey));
                    secureStorage.store("humanid", cipheletAPI.humanID);
                    System.out.println("Registered as: " + cipheletAPI.humanID);
                    MainActivity.getInstance().loggedIn(cipheletAPI);
                }
            }).start();
        } catch (URISyntaxException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
