package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class VerificationResponse {


    private boolean verified;
    private String rid;

    public boolean isVerified() {
        return verified;
    }

    public String getRegistrationId(){
        return rid;
    }


}
