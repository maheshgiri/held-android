package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.held.activity.R;
import com.held.adapters.ActivityFeedAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ActivityFeedData;
import com.held.retrofit.response.ActivityFeedDataResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ActivityFeedFragment extends ParentFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActivityFeedAdapter mActivityFeedAdapter;
    private List<ActivityFeedData> mActivityFeedDataList = new ArrayList<>();
    private boolean mIsLastPage, mIsLoading = true;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 7;
    private String mUid;

    public static final String TAG = ActivityFeedFragment.class.getSimpleName();

    public static ActivityFeedFragment newInstance(String uid) {
        ActivityFeedFragment activityFeedFragment = new ActivityFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        activityFeedFragment.setArguments(bundle);
        return activityFeedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_feed, container, false);

    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.FEED_rc_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mActivityFeedAdapter = new ActivityFeedAdapter(getCurrActivity(), mActivityFeedDataList, mIsLastPage);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mActivityFeedAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FEED_swipe_refresh_layout);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    callActivityFeedApi();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getCurrActivity().getNetworkStatus()) {
                    mIsLastPage = false;
                    mActivityFeedDataList.clear();
                    mStart = System.currentTimeMillis();
//                    DialogUtils.showProgressBar();
                    callActivityFeedApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        if (getCurrActivity().getNetworkStatus()) {
            callSearcUserApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet");
        }
    }

    private void callSearcUserApi() {
        HeldService.getService().searchUser(PreferenceHelper.getInstance(getCurrActivity())
                .readPreference(getString(R.string.API_session_token)), PreferenceHelper.getInstance(getCurrActivity())
                .readPreference(getString(R.string.API_user_name)), new Callback<SearchUserResponse>() {
            @Override
            public void success(SearchUserResponse searchUserResponse, Response response) {
                mUid = searchUserResponse.getRid();
                callActivityFeedApi();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getResponse() != null &&
                        !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                                UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                    if (json.substring(json.indexOf(":") + 2, json.length() - 2).equals("")) {
                    }
                } else
                    UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
            }
        });
    }

    private void callActivityFeedApi() {
        HeldService.getService().getActivitiesFeed(PreferenceHelper.getInstance(getCurrActivity())
                .readPreference(getString(R.string.API_session_token)), mLimit, mStart, mUid, new Callback<ActivityFeedDataResponse>() {
            @Override
            public void success(ActivityFeedDataResponse activityFeedDataResponse, Response response) {
                mActivityFeedDataList.addAll(activityFeedDataResponse.getObjects());
                mIsLastPage = activityFeedDataResponse.isLastPage();
                mActivityFeedAdapter.setActivityFeedList(mActivityFeedDataList, mIsLastPage);
                mStart = activityFeedDataResponse.getNextPageStart();
                mIsLoading = false;
            }

            @Override
            public void failure(RetrofitError error) {
                mIsLoading = false;
                if (error != null && error.getResponse() != null &&
                        !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                                UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                    if (json.substring(json.indexOf(":") + 2, json.length() - 2).equals("")) {
                    }
                } else
                    UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
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
