package com.held.retrofit.response;


import java.util.List;

public class FeedResponse {

    private List<FeedData> objects;
    private boolean lastPage;
    private long nextPageStart;
    String next;

    public String getNext() {
        return next;
    }
    public void setNext(String next) {
        this.next = next;
    }

    public long getNextPageStart() {
        return nextPageStart;
    }
    public boolean isLastPage() {
        return lastPage;
    }
    public List<FeedData> getObjects() {
        return objects;
    }


}
