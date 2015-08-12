package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class LoginUserResponse {

    private String session_token;
    private boolean login;

    public String getSession_token() {
        return session_token;
    }

    public boolean isLogin() {
        return login;
    }
}
