package com.github.worldoneo.ciphelet.listener

import android.view.View
import android.widget.EditText
import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.MainActivity
import com.github.worldoneo.ciphelet.R
import com.github.worldoneo.ciphelet.connector.CipheletAPI
import com.github.worldoneo.ciphelet.connector.Connector
import com.github.worldoneo.ciphelet.connector.RegistrationService
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility
import com.github.worldoneo.ciphelet.connector.events.EventManager.handleEvent
import com.github.worldoneo.ciphelet.connector.events.LoginSuccessEvent
import com.github.worldoneo.ciphelet.connector.events.RegisterSuccessEvent
import com.github.worldoneo.ciphelet.storage.SecureStorage
import java.lang.RuntimeException
import java.net.URI
import java.net.URISyntaxException

class RegisterClickListener : View.OnClickListener {
    override fun onClick(v: View) {
        val registerInput = MainActivity.instance!!.findViewById<View>(R.id.registerPasswordInput)
        val password = (registerInput as EditText).text.toString()
        if (password == "") {
            registerInput.setBackgroundColor(-0x5c5d)
            return
        }
        v.isEnabled = false
        try {
            val preferences = MainActivity.instance!!.preferences
            val uri = URI(MainActivity.instance!!.getStringsxml(R.string.server))
            Thread {
                val secretKey = EncryptionUtility.getKeyFromPassword(password, SecureStorage.getSalt(preferences))
                        ?: throw RuntimeException()
                val secureStorage = SecureStorage(preferences, secretKey)
                val connector = Connector(uri)
                val r = RegistrationService(connector, Consumer { cipheletAPI ->
                    secureStorage.store("privatekey", EncryptionUtility.encodeKey(cipheletAPI.privateKey))
                    secureStorage.store("userid", cipheletAPI.userid)
                    println("Registered as: " + cipheletAPI.userid)
                    handleEvent(RegisterSuccessEvent())
                    handleEvent(LoginSuccessEvent(cipheletAPI))
                })
                r.register(password)
            }.start()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }
}