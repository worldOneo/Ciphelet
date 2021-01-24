package com.github.worldoneo.ciphelet.connector.events

import com.github.worldoneo.ciphelet.connector.action.ChallengeAction

data class ChallengeSuccessEvent(val challengeAction: ChallengeAction) : Event