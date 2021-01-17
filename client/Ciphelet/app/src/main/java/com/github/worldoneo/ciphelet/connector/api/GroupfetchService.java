package com.github.worldoneo.ciphelet.connector.api;

import com.github.worldoneo.ciphelet.connector.CipheletAPI;
import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.action.GenericAction;

import java.util.Arrays;

public class GroupfetchService extends Service<long[]> {
    private long[] result;
    public GroupfetchService(Connector connector, CipheletAPI cipheletAPI) {
        super(connector, cipheletAPI, GenericAction.Action.GROUPFETCH);
    }

    @Override
    public long[] getResults() {
        return result;
    }

    @Override
    public void receivedAction(GenericAction action) {
        System.out.println("Groups fetched " + Arrays.toString(action.groupfetchAction.chatid));
        result = action.groupfetchAction.chatid;
    }

    @Override
    public void run() {
        GenericAction genericAction = new GenericAction(GenericAction.Action.GROUPFETCH.request);
        connector.sendAction(genericAction);
    }
}
