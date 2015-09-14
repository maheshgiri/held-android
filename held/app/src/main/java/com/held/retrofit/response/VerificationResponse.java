package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class VerificationResponse {


    private boolean verified;
    private String session_token;

    public String getSession_token() {
        return session_token;
    }
    public boolean isVerified() {
        return verified;
    }
}
