package com.held.retrofit.response;


import java.util.List;

public class FriendRequestResponse {

    private List<SearchUserResponse> objects;
    private boolean lastPage;

    public List<SearchUserResponse> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }
}
