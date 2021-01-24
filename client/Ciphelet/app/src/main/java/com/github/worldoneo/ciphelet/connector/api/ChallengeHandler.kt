package com.github.worldoneo.ciphelet.connector.api

import android.util.Base64
import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.connector.Connector
import com.github.worldoneo.ciphelet.connector.action.ChallengeAction
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.decodeKey
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.decryptNaCL

class ChallengeHandler(private val privateKey: ByteArray?, private val connector: Connector) : Consumer<GenericAction> {
    private var callback: Runnable? = null

    constructor(privateKey: ByteArray?, connector: Connector, callback: Runnable?) : this(privateKey, connector) {
        this.callback = callback
    }

    override fun accept(genericAction: GenericAction) {
        if (genericAction.action != GenericAction.CHALLENGE_ACTION) {
            println("Action listener recieved wrong action")
            return
        }
        val requestedAction = genericAction.challengeAction!!
        val response = GenericAction(GenericAction.CHALLENGE_ACTION)
        val challengeAction = ChallengeAction()
        val decrypted = decryptNaCL(
                Base64.decode(requestedAction.token!!.toByteArray(), Base64.DEFAULT),
                decodeKey(requestedAction.publickey), privateKey)
        challengeAction.token = String(decrypted)
        response.challengeAction = challengeAction
        connector.sendAction(response)
        if (callback != null) {
            callback!!.run()
        }
    }

}