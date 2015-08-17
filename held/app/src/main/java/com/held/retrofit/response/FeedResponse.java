package com.held.retrofit.response;


import java.util.List;

public class FeedResponse {

    private List<FeedData> objects;
    private boolean lastPage;
    private long nextPageStart;

    public long getNextPageStart() {
        return nextPageStart;
    }

    public void setNextPageStart(long nextPageStart) {
        this.nextPageStart = nextPageStart;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public List<FeedData> getObjects() {
        return objects;
    }

    public void setObjects(List<FeedData> objects) {
        this.objects = objects;
    }
}
