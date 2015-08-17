package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.adapters.FriendsAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FriendsListFragment extends ParentFragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FriendsAdapter mFriendAdapter;
    private List<SearchUserResponse> mFriendList = new ArrayList<>();
    private boolean mIsLastPage, mIsLoading;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 8;

    public static final String TAG = FriendsListFragment.class.getSimpleName();

    public static FriendsListFragment newInstance() {
        return new FriendsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.FRIENDLIST_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriendAdapter = new FriendsAdapter((PostActivity) getCurrActivity(), mFriendList, mIsLastPage);
        mRecyclerView.setAdapter(mFriendAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FRIENDLIST_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStart = System.currentTimeMillis();
                mIsLastPage = false;
                mSwipeRefreshLayout.setRefreshing(false);
                mFriendList.clear();
                if (getCurrActivity().getNetworkStatus()) {
                    callFriendsListApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet");
                }
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    callFriendsListApi();
                }
            }
        });
        if (getCurrActivity().getNetworkStatus()) {
            callFriendsListApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
        }
    }

    private void callFriendsListApi() {
        mIsLoading = true;
        HeldService.getService().getFriendsList(PreferenceHelper.getInstance(getCurrActivity())
                .readPreference(getString(R.string.API_session_token)), mLimit, mStart, new Callback<FriendRequestResponse>() {
            @Override
            public void success(FriendRequestResponse friendRequestResponse, Response response) {
                mIsLastPage = friendRequestResponse.isLastPage();
                mFriendList.addAll(friendRequestResponse.getObjects());
                mStart = friendRequestResponse.getNextPageStart();
                mFriendAdapter.setFriendList(mFriendList, mIsLastPage);
                mIsLoading = false;
            }

            @Override
            public void failure(RetrofitError error) {
                mIsLoading = false;
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
