package com.github.worldoneo.ciphelet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.listener.LoggedInListener;
import com.github.worldoneo.ciphelet.listener.LoginClickListener;
import com.github.worldoneo.ciphelet.listener.RegisterClickListener;

import lombok.Setter;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        if (getPreferences().getString("privatekey", "").equals("")) {
            setContentView(R.layout.activity_main);
            final Button register = findViewById(R.id.registerButton);
            register.setOnClickListener(new RegisterClickListener());
        } else {
            setContentView(R.layout.activity_login);
            final Button login = findViewById(R.id.loginButton);
            login.setOnClickListener(new LoginClickListener());
        }
    }

    public void loggedIn(CipheletAPI api) {
        runOnUiThread(new LoggedInListener(api));
    }

    public String getStringsxml(int id) {
        return getResources().getString(id);
    }

    public SharedPreferences getPreferences() {
        return getPreferences(MODE_PRIVATE);
    }
}
