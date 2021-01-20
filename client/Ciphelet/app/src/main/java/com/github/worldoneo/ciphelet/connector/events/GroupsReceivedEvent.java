package com.github.worldoneo.ciphelet.connector.events;

public class GroupsReceivedEvent implements Event {
    private final long[] groups;

    public GroupsReceivedEvent(long[] groups) {
        this.groups = groups;
    }

    public long[] getGroups() {
        return groups;
    }
}

