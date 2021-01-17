package com.github.worldoneo.ciphelet.connector.action;

public class GenericAction {
    public static final String REGISTER_ACTION = "register";
    public static final String LOGIN_ACTION = "login";
    public static final String CHALLENGE_ACTION = "challenge";
    public static final String GROUPFETCH_ACTION = "groupfetch";
    public static final String SUCCESS = "success";
    public String action;
    public ChallengeAction challengeAction;
    public RegisterAction registerAction;
    public LoginAction loginAction;
    public GroupfetchAction groupfetchAction;

    public GenericAction(String action) {
        this.action = action;
    }

    public enum Action {
        REGISTER(REGISTER_ACTION),
        LOGIN(LOGIN_ACTION),
        CHALLENGE(CHALLENGE_ACTION, CHALLENGE_ACTION),
        GROUPFETCH(GROUPFETCH_ACTION);
        public final String request;
        public final String response;

        Action(String request) {
            this.request = request;
            this.response = request + SUCCESS;
        }

        Action(String request, String response) {
            this.request = request;
            this.response = response;
        }
    }
}
