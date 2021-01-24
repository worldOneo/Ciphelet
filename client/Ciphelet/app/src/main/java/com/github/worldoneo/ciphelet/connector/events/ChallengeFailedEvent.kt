package com.github.worldoneo.ciphelet.connector.events

import com.github.worldoneo.ciphelet.connector.action.ChallengeAction

data class ChallengeFailedEvent(val challengeAction: ChallengeAction) : Event