package com.github.worldoneo.ciphelet.connector

import androidx.core.util.Consumer
import com.github.worldoneo.ciphelet.connector.action.GenericAction
import com.github.worldoneo.ciphelet.connector.action.LoginAction
import com.github.worldoneo.ciphelet.connector.api.ChallengeService
import com.github.worldoneo.ciphelet.connector.api.GroupfetchService
import com.github.worldoneo.ciphelet.connector.api.LoginRequiredService
import com.github.worldoneo.ciphelet.connector.api.Service
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility
import java.util.*
import java.util.concurrent.Executors

class CipheletAPI(val userid: Long, private val connector: Connector, val privateKey: ByteArray) {
    private val services: MutableMap<String, Service> = HashMap()
    private val executorService = Executors.newCachedThreadPool()
    private val loggedIn = false
    private var onLogin: Consumer<CipheletAPI>? = null
    private fun fetch() {
        executorService.submit { services[GenericAction.Action.GROUPFETCH.response]!!.run() }
    }

    fun login(password: String?) {
        if (!connector.Connect()) {
            System.err.println("Couldn't connect to server!")
            return
        }
        val genericAction = GenericAction(GenericAction.LOGIN_ACTION)
        val loginAction = LoginAction()
        println("Logging in as: " + userid)
        loginAction.userid = userid
        loginAction.password = password
        genericAction.loginAction = loginAction
        connector.sendAction(genericAction)
        val api = this
        connector.once(GenericAction.CHALLENGE_ACTION, Consumer {
            challengeDone()
            onLogin!!.accept(api)
        })
    }

    private fun challengeDone() {
        println("Logged in")
        fetch()
    }

    fun onLogin(onLogin: Consumer<CipheletAPI>) {
        this.onLogin = onLogin
    }

    fun registerService(service: Service) {
        services[service.recievingPacket] = service
        connector.actionHook(service.recievingPacket, service)
    }

    init {
        registerService(ChallengeService(connector, this))
        registerService(GroupfetchService(connector, this))
        registerService(LoginRequiredService(connector, this))
    }
}