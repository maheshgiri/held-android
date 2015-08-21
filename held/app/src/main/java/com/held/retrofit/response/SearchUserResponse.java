package com.held.retrofit.response;


public class SearchUserResponse {

    private String session_token;
    private int pin;
    private String phone;
    private String join_date;
    private boolean verified;
    private boolean online;
    private boolean banned;
    private String pic;
    private String rid;
    private String display_name;

    public String getNotification_token() {
        return notification_token;
    }

    private String notification_token;

    public String getSession_token() {
        return session_token;
    }

    public int getPin() {
        return pin;
    }

    public String getPhone() {
        return phone;
    }

    public String getJoin_date() {
        return join_date;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isBanned() {
        return banned;
    }

    public String getPic() {
        return pic;
    }

    public String getRid() {
        return rid;
    }

    public String getDisplay_name() {
        return display_name;
    }
}
