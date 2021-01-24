package com.github.worldoneo.ciphelet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.worldoneo.ciphelet.connector.events.EventManager.registerEvent
import com.github.worldoneo.ciphelet.listener.LoggedInListener
import com.github.worldoneo.ciphelet.listener.LoginClickListener
import com.github.worldoneo.ciphelet.listener.RegisterClickListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this
        super.onCreate(savedInstanceState)
        if (preferences.getString("privatekey", "") == "") {
            setContentView(R.layout.activity_main)
            val register = findViewById<Button>(R.id.registerButton)
            register.setOnClickListener(RegisterClickListener())
        } else {
            setContentView(R.layout.activity_login)
            val login = findViewById<Button>(R.id.loginButton)
            login.setOnClickListener(LoginClickListener())
        }
        registerEvent(LoggedInListener())
    }

    fun getStringsxml(id: Int): String {
        return resources.getString(id)
    }

    val preferences: SharedPreferences
        get() = getPreferences(Context.MODE_PRIVATE)

    companion object {
        var instance: MainActivity? = null
    }

}