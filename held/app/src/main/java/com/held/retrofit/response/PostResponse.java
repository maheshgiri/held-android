package com.held.retrofit.response;

import android.media.Image;

/**
 * Created by jay on 3/8/15.
 */
public class PostResponse {

    String date;
    String imageUri;
    int held;
    String text;
    String rid;
    String owner_display_name;
    String thumbnailUri;

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public String getOwner_display_name() {
        return owner_display_name;
    }

    public String getDate() {
        return date;
    }

    public String getImageUri() {
        return imageUri;
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
