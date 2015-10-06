package com.held.retrofit.response;


public class ActivityFeedData {

    private String date;
    private String owner_display_name;
    private String text;
    private String rid,postPic;

    public String getPostPic() {
        return postPic;
    }

    public String getDate() {
        return date;
    }

    public String getOwner_display_name() {
        return owner_display_name;
    }

    public String getText() {
        return text;
    }

    public String getRid() {
        return rid;
    }
}
