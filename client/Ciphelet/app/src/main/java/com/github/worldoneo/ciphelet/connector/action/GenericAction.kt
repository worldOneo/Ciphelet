package com.github.worldoneo.ciphelet.connector.action

class GenericAction(var action: String) {
    var challengeAction: ChallengeAction? = null
    var registerAction: RegisterAction? = null
    var loginAction: LoginAction? = null
    var groupfetchAction: GroupfetchAction? = null

    enum class Action {
        REGISTER(REGISTER_ACTION), LOGIN_REQUIRED(LOGIN_ACTION, LOGIN_ACTION), LOGIN(LOGIN_ACTION), CHALLENGE(CHALLENGE_ACTION, CHALLENGE_ACTION), GROUPFETCH(GROUPFETCH_ACTION);

        val request: String
        val response: String

        constructor(request: String) {
            this.request = request
            response = request + SUCCESS
        }

        constructor(request: String, response: String) {
            this.request = request
            this.response = response
        }
    }

    companion object {
        const val REGISTER_ACTION = "register"
        const val LOGIN_ACTION = "login"
        const val CHALLENGE_ACTION = "challenge"
        const val GROUPFETCH_ACTION = "groupfetch"
        const val SUCCESS = "success"
    }

}