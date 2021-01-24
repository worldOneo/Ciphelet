package com.github.worldoneo.ciphelet.connector.action

class RegisterAction(var password: String,
                     var key: String? = null,
                     var userid: Long = 0) : IAction