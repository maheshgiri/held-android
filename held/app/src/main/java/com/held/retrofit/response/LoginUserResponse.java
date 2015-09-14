package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class LoginUserResponse {

    private String sessionToken;
    private boolean login;



    public String getSessionToken() {
        return sessionToken;
    }
    public boolean isLogin() {
        return login;
    }
}
