package com.held.retrofit.response;


public class PostChatData {

    private String tag;
    private String text;
    private User toUser,fromUser;
    private String date,rid;


    public String getDate() {
        return date;
    }


    public String getTag() {
        return tag;
    }

    public String getRid() {
        return rid;
    }

    public String getText() {
        return text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}
