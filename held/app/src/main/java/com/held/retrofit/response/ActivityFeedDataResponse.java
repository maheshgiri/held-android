package com.held.retrofit.response;


import java.util.List;

public class ActivityFeedDataResponse {

    private List<ActivityFeedData> objects;
    private boolean lastPage;
    private long nextPageStart;

    public List<ActivityFeedData> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public long getNextPageStart() {
        return nextPageStart;
    }
}
