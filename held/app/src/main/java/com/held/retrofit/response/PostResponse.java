package com.held.retrofit.response;

/**
 * Created by jay on 3/8/15.
 */
public class PostResponse {

    String date;
    String image;
    int held;
    String text;
    String rid;

    public String getOwner_display_name() {
        return owner_display_name;
    }

    String owner_display_name;

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public int getHeld() {
        return held;
    }

    public String getText() {
        return text;
    }

    public String getRid() {
        return rid;
    }
}
