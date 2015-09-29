package com.held.retrofit.response;


import java.util.List;

public class FriendRequestResponse {


    private List<Objects> objects;
    private boolean lastPage;
    private long nextPageStart;

    public long getNextPageStart() {
        return nextPageStart;
    }

    public List<Objects> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }


}
