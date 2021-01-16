package com.github.worldoneo.ciphelet.listener;

import android.widget.TextView;

import com.github.worldoneo.ciphelet.MainActivity;
import com.github.worldoneo.ciphelet.R;
import com.github.worldoneo.ciphelet.connector.CipheletAPI;

public class LoggedInListener implements Runnable {
    private final CipheletAPI cipheletAPI;

    public LoggedInListener(CipheletAPI cipheletAPI) {
        this.cipheletAPI = cipheletAPI;
    }

    @Override
    public void run() {
        cipheletAPI.hook();
        MainActivity instance = MainActivity.getInstance();
        instance.setContentView(R.layout.activity_welcome);
        ((TextView) instance.findViewById(R.id.welcomeText)).setText(String.format(instance.getStringsxml(R.string.welcome), cipheletAPI.humanID));
    }
}
