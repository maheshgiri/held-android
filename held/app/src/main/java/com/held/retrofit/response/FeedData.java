package com.held.retrofit.response;


public class FeedData {

    private String date;
    private String imageUri;
    private long held;
private String text;
    private String profilePic;
    private String thumbnailUri;
    private User user;
    private Creator creator;

    public String getThumbnailUri() {
        return thumbnailUri;
    }
    public String getDate() {
        return date;
    }
    public String getImageUri() {
        return imageUri;
    }
    public long getHeld() {
        return held;
    }

    public String getText() {
        return text;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public User getUser() {
        return user;
    }

    public Creator getCreator() {
        return creator;
    }
}

