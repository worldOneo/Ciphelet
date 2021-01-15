package com.github.worldoneo.ciphelet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.ConnectorThread;
import com.github.worldoneo.ciphelet.connector.RegistrationService;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            new ConnectorThread(getPreferences(MODE_PRIVATE), new URI(getResources().getString(R.string.server))).start();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String getStringsxml(int id) {
        return getResources().getString(id);
    }
}
