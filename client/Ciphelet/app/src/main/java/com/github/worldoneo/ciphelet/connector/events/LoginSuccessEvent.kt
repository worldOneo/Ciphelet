package com.github.worldoneo.ciphelet.connector.events

import com.github.worldoneo.ciphelet.connector.CipheletAPI

data class LoginSuccessEvent(val cipheletAPI: CipheletAPI) : Event