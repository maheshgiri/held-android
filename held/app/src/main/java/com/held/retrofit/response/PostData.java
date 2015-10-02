package com.held.retrofit.response;

/**
 * Created by MAHESH on 9/30/2015.
 */
public class PostData {

    private String thumbnailUri,text,rid;
    private Creator creator;


    public Creator getCreator() {
        return creator;
    }

    public String getText() {
        return text;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public String getRid() {
        return rid;
    }
}
