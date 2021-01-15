package com.github.worldoneo.ciphelet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.worldoneo.ciphelet.connector.Connector;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            final URI u = new URI("ws://192.168.178.75:8080/ws");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connector connector = new Connector(u);
                    connector.RegisterHandShake();
                }
            }).start();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
