package com.held.retrofit.response;

import java.util.ArrayList;

/**
 * Created by MAHESH on 11/21/2015.
 */
public class InviteListResponse {
    ArrayList<InviteResponse> objects;

    public ArrayList<InviteResponse> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<InviteResponse> objects) {
        this.objects = objects;
    }
}
