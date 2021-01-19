package com.github.worldoneo.ciphelet.connector.events;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;

public class LoginSuccessEvent implements Event {
    private final CipheletAPI cipheletAPI;

    public LoginSuccessEvent(CipheletAPI cipheletAPI) {
        this.cipheletAPI = cipheletAPI;
    }

    public CipheletAPI getCipheletAPI() {
        return cipheletAPI;
    }
}
