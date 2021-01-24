package com.github.worldoneo.ciphelet.connector

import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.github.worldoneo.ciphelet.connector.action.RegisterAction
import com.github.worldoneo.ciphelet.connector.api.ChallengeHandler
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility

class RegistrationService(private val connector: Connector, private val onRegister: Consumer<CipheletAPI>) {
    fun register(password: String) {
        connector.Connect()
        println("Register0")
        val keyPair = EncryptionUtility.GenerateKeypair()
        val genericAction = GenericAction("register")
        val rAction = RegisterAction(password)
        val pKey = keyPair.publicKey
        rAction.key = EncryptionUtility.encodeKey(pKey)
        genericAction.registerAction = rAction
        val privateKey = keyPair.secretKey
        connector.once(GenericAction.Action.REGISTER.response, Consumer { onRegister.accept(CipheletAPI(genericAction.registerAction!!.userid, connector, privateKey)) })
        connector.once(GenericAction.Action.CHALLENGE.response, ChallengeHandler(privateKey, connector))
        connector.sendAction(genericAction)
    }

}