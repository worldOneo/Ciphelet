package com.github.worldoneo.ciphelet.connector.api

import android.util.Base64
import com.github.worldoneo.ciphelet.connector.CipheletAPI
import com.github.worldoneo.ciphelet.connector.Connector
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.decodeKey
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.decryptNaCL
import com.github.worldoneo.ciphelet.connector.events.ChallengeSuccessEvent
import com.github.worldoneo.ciphelet.connector.events.EventManager.handleEvent

class ChallengeService(connector: Connector?, cipheletAPI: CipheletAPI) : Service(connector, cipheletAPI, GenericAction.Action.CHALLENGE) {
    override fun receivedAction(action: GenericAction) {
        val cAction = action.challengeAction!!
        println("Challenge handled")
        val response = GenericAction(GenericAction.CHALLENGE_ACTION)
        val challengeAction = ChallengeAction()
        val bytes = decryptNaCL(
                Base64.decode(cAction.token!!.toByteArray(), Base64.DEFAULT),
                decodeKey(cAction.publickey), cipheletAPI.privateKey)
        challengeAction.token = String(bytes)
        response.challengeAction = challengeAction
        connector!!.sendAction(response)
        handleEvent(ChallengeSuccessEvent(challengeAction))
    }
}