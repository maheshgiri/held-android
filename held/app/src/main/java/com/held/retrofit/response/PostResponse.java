package com.held.retrofit.response;

import android.media.Image;

/**
 * Created by jay on 3/8/15.
 */
public class PostResponse {

    String date;
    Image image;
    int held;
    String text;
    String rid;
    String owner_display_name;
    String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public String getOwner_display_name() {
        return owner_display_name;
    }

    public String getDate() {
        return date;
    }

    public Image getImage() {
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
