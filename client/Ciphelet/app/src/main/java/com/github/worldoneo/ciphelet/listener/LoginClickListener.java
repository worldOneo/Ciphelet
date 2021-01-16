package com.github.worldoneo.ciphelet.listener;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import com.github.worldoneo.ciphelet.MainActivity;
import com.github.worldoneo.ciphelet.R;
import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.ConnectorThread;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;
import com.github.worldoneo.ciphelet.storage.SecureStorage;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        MainActivity instance = MainActivity.getInstance();
        View loginInput = instance.findViewById(R.id.loginPasswordInput);
        String password = ((EditText) loginInput).getText().toString();
        if (password.equals("")) {
            loginInput.setBackgroundColor(0xFFFFA3A3);
            return;
        }
        v.setEnabled(false);
        try {
            SharedPreferences preferences = instance.getPreferences();
            URI uri = new URI(instance.getStringsxml(R.string.server));
            SecureStorage secureStorage = new SecureStorage(preferences, EncryptionUtility.getKeyFromPassword(password, SecureStorage.getSalt(preferences)));
            new ConnectorThread(password, uri, secureStorage).start();
        } catch (URISyntaxException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}