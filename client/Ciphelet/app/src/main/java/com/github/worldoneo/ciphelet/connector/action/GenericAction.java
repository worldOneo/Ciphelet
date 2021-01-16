package com.github.worldoneo.ciphelet.connector.action;

public class GenericAction {
    public String action;
    public ChallengeAction challengeAction;
    public RegisterAction registerAction;
    public LoginAction loginAction;
    public ChatfetchAction chatfetchAction;

    public static final String RegisterAction = "register";
    public static final String LoginAction = "login";
    public static final String ChallengeAction = "challenge";
    public static final String ChatfetchAction = "chatfetch";

    public GenericAction(String action) {
        this.action = action;
    }
}
