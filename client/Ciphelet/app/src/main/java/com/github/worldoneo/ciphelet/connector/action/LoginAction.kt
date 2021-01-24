package com.github.worldoneo.ciphelet.connector.action

data class LoginAction(var userid: Long = 0,
                       var password: String? = null) : IAction