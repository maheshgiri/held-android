package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.adapters.FeedAdapter;
import com.held.customview.BlurTransformation;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FeedFragment extends ParentFragment {

    public static final String TAG = FeedFragment.class.getSimpleName();

    private RecyclerView mFeedRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FeedAdapter mFeedAdapter;
    private FeedResponse mFeedResponse;
    private BlurTransformation blurTransformation;
    private GestureDetector gestureDetector;
    private EditText mSearchEdt;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mFeedRecyclerView = (RecyclerView) view.findViewById(R.id.FEED_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mFeedRecyclerView.setLayoutManager(mLayoutManager);
        mFeedResponse = new FeedResponse();
        blurTransformation = new BlurTransformation(getCurrActivity(), 25f);
        mFeedAdapter = new FeedAdapter((PostActivity) getCurrActivity(), mFeedResponse, blurTransformation);
        mFeedRecyclerView.setAdapter(mFeedAdapter);
        getCurrActivity().findViewById(R.id.TOOLBAR_camera_img).setOnClickListener(this);
        if (getCurrActivity().getNetworkStatus()) {
            DialogUtils.showProgressBar();
            callFeedApi();
        } else
            UiUtils.showSnackbarToast(getView(), "Sorry! You don't seem to connected to internet");
        mSearchEdt = (EditText) getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_search_edt);
        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getCurrActivity().getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callUserSearchApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FEED_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                mSearchEdt.getText().toString().trim(), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        Utils.hideSoftKeyboard(getCurrActivity());
                        Bundle bundle = new Bundle();
                        bundle.putString("name", searchUserResponse.getDisplay_name());
                        bundle.putString("image", searchUserResponse.getPic());
                        getCurrActivity().perform(5, bundle);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (!TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void callFeedApi() {
        if (getCurrActivity().getNetworkStatus()) {//PreferenceHelper.getInstance(getCurrActivity()).readPreference("SESSION_TOKEN")
            HeldService.getService().feedPost(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)), new Callback<FeedResponse>() {
                @Override
                public void success(FeedResponse feedResponse, Response response) {
                    DialogUtils.stopProgressDialog();
                    mFeedResponse = feedResponse;
                    mFeedAdapter.setFeedResponse(feedResponse);
                }

                @Override
                public void failure(RetrofitError error) {
                    DialogUtils.stopProgressDialog();
                    if (!TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                    } else
                        UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                }
            });
        }
    }

    @Override
    protected void bindListeners(View view) {
        getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_chat_img).setOnClickListener(this);
        getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_notification_img).setOnClickListener(this);
    }

    @Override
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.TOOLBAR_camera_img:
                getCurrActivity().perform(3, null);
                break;
            case R.id.TOOLBAR_chat_img:
                Log.d("CHat", "Clcicked");
                break;
            case R.id.TOOLBAR_notification_img:
                getCurrActivity().perform(4, null);
                break;
        }
    }
}
