package com.held.retrofit.response;

import java.util.List;

/**
 * Created by MAHESH on 10/16/2015.
 */
public class SearchByNameResponce {
    private List<Engager> objects;
    private Engager engager;

    public Engager getEngager() {
        return engager;
    }

    public List<Engager> getObjects() {
        return objects;
    }
}
