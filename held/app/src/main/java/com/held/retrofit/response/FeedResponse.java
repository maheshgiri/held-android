package com.held.retrofit.response;


import java.util.List;

public class FeedResponse {

    private List<FeedData> objects;
    private boolean lastPage;
    private long nextPageStart;


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
