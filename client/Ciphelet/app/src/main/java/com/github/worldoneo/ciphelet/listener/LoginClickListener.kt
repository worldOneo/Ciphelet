package com.github.worldoneo.ciphelet.listener

import android.view.View
import android.widget.EditText
import com.github.worldoneo.ciphelet.MainActivity
import com.github.worldoneo.ciphelet.R
import com.github.worldoneo.ciphelet.connector.ConnectorThread
import java.net.URI
import java.net.URISyntaxException

class LoginClickListener : View.OnClickListener {
    override fun onClick(v: View) {
        println("Clicked login button")
        val loginInput = MainActivity.instance!!.findViewById<View>(R.id.loginPasswordInput)
        val password = (loginInput as EditText).text.toString()
        if (password == "") {
            loginInput.setBackgroundColor(0xFFFFA3A3.toInt())
            return
        }
        v.isEnabled = false
        println("Trying")
        try {
            val preferences = MainActivity.instance!!.preferences
            val uri = URI(MainActivity.instance!!.getStringsxml(R.string.server))
            println("Setting up secureStorage")
            println("Desyncing the universe!")
            ConnectorThread(password, uri, preferences).start()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }
}