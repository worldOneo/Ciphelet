package com.github.worldoneo.ciphelet.connector.events;

import com.github.worldoneo.ciphelet.connector.action.ChallengeAction;

public class ChallengeSuccessEvent implements Event {
    public ChallengeAction challengeAction = new ChallengeAction();

    public ChallengeSuccessEvent(ChallengeAction challengeAction) {
        this.challengeAction = challengeAction;
    }

    public ChallengeAction getChallengeAction() {
        return challengeAction;
    }
}
