package com.github.worldoneo.ciphelet.connector.hooks;

import androidx.core.util.Consumer;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;

public class ChatfetchHook implements Consumer<GenericAction> {
    private CipheletAPI cipheletAPI;

    public ChatfetchHook(CipheletAPI cipheletAPI) {
        this.cipheletAPI = cipheletAPI;
    }

    @Override
    public void accept(GenericAction genericAction) {
        if (!genericAction.action.equals(GenericAction.ChatfetchAction)) {
            return;
        }
        cipheletAPI.setChatids(genericAction.chatfetchAction.chatid);
    }

}
