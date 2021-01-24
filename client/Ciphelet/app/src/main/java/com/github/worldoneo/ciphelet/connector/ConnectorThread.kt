package com.github.worldoneo.ciphelet.connector

import android.content.SharedPreferences
import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.MainActivity
import com.github.worldoneo.ciphelet.R
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility
import com.github.worldoneo.ciphelet.connector.events.EventManager.handleEvent
import com.github.worldoneo.ciphelet.connector.events.LoginSuccessEvent
import com.github.worldoneo.ciphelet.storage.SecureStorage
import java.lang.RuntimeException
import java.net.URI
import java.net.URISyntaxException

class ConnectorThread(private val password: String, private val u: URI, private val preferences: SharedPreferences) : Thread() {
    private var secureStorage: SecureStorage? = null
    override fun run() {
        val key = EncryptionUtility.getKeyFromPassword(password, SecureStorage.getSalt(preferences))
                ?: throw RuntimeException()
        secureStorage = SecureStorage(preferences, key)
        println("Going async!")
        var uri: URI? = null
        try {
            uri = URI(MainActivity.instance!!.getStringsxml(R.string.server))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        val userid = secureStorage!!.get("userid", Long::class.java) ?: throw RuntimeException()
        val cipheletAPI = CipheletAPI(
                userid,
                Connector(uri),
                EncryptionUtility.decodeKey(secureStorage!!.get("privatekey", String::class.java)))
        cipheletAPI.onLogin(Consumer { handleEvent(LoginSuccessEvent(cipheletAPI)) })
        cipheletAPI.login(password)
    }

}