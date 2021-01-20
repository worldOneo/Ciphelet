package com.github.worldoneo.ciphelet.connector.api;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;
import com.github.worldoneo.ciphelet.connector.events.EventManager;
import com.github.worldoneo.ciphelet.connector.events.GroupsReceivedEvent;

import java.util.Arrays;

public class GroupfetchService extends Service {
    public GroupfetchService(Connector connector, CipheletAPI cipheletAPI) {
        super(connector, cipheletAPI, GenericAction.Action.GROUPFETCH);
    }

    @Override
    public void receivedAction(GenericAction action) {
        System.out.println("Groups fetched " + Arrays.toString(action.groupfetchAction.chatid));
        EventManager.getInstance().handleEvent(new GroupsReceivedEvent(action.groupfetchAction.chatid));
    }

    @Override
    public void run() {
        GenericAction genericAction = new GenericAction(GenericAction.Action.GROUPFETCH.request);
        connector.sendAction(genericAction);
    }
}
