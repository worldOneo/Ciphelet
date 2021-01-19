package com.github.worldoneo.ciphelet.listener;

import android.widget.TextView;

import com.github.worldoneo.ciphelet.MainActivity;
import com.github.worldoneo.ciphelet.R;
import com.github.worldoneo.ciphelet.connector.events.EventHandler;
import com.github.worldoneo.ciphelet.connector.events.EventListener;
import com.github.worldoneo.ciphelet.connector.events.LoginSuccessEvent;

public class LoggedInListener extends EventListener {

    public LoggedInListener() {
    }

    @EventHandler
    public void onLogin(LoginSuccessEvent event) {
        MainActivity instance = MainActivity.getInstance();
        instance.setContentView(R.layout.activity_welcome);
        ((TextView) instance.findViewById(R.id.welcomeText)).setText(String.format(instance.getStringsxml(R.string.welcome), event.getCipheletAPI().humanID));
    }
}
