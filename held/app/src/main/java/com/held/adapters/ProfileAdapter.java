package com.held.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.held.activity.ParentActivity;
import com.held.retrofit.response.FeedData;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter {

    private ParentActivity activity;
    private 

    public ProfileAdapter(ParentActivity activity,List<FeedData> feedDataList)

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
