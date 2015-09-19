package com.held.retrofit.response;

public class ReleaseResponse {

    private int held;
    String text;
    String rid;
    String display_name;
    String profilePic;
    String thumbnailUri;

    public int getHeld() {
        return held;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getRid() {
        return rid;
    }

    public String getText() {
        return text;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }
}
