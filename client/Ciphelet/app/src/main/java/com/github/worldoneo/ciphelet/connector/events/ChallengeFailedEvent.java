package com.github.worldoneo.ciphelet.connector.events;

import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;

public class ChallengeFailedEvent implements Event {
    private ChallengeAction challengeAction = new ChallengeAction();

    public ChallengeFailedEvent(ChallengeAction challengeAction) {
        this.challengeAction = challengeAction;
    }

    public ChallengeAction getChallengeAction() {
        return challengeAction;
    }
}
