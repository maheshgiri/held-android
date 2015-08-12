package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.adapters.FriendRequestAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FriendRequestFragment extends ParentFragment {

    public static final String TAG = FriendRequestFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<SearchUserResponse> mFriendRequestList = new ArrayList<>();
    private FriendRequestAdapter mFriendRequestAdapter;

    public static FriendRequestFragment newInstance() {
        return new FriendRequestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.FR_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mFriendRequestAdapter = new FriendRequestAdapter((PostActivity) getCurrActivity(), mFriendRequestList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mFriendRequestAdapter);
        callFriendRequestListApi();
    }

    private void callFriendRequestListApi() {
        HeldService.getService().getFriendRequests(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)), new Callback<FriendRequestResponse>() {
            @Override
            public void success(FriendRequestResponse friendRequestResponse, Response response) {
                mFriendRequestAdapter.setFriendRequestList(friendRequestResponse.getObjects());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {

    }
}
