package com.github.worldoneo.ciphelet.connector.action

data class ChallengeAction(var token: String? = null,
                           var publickey: String? = null) : IAction