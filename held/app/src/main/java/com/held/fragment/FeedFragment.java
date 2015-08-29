package com.held.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.FeedActivity;
import com.held.activity.R;
import com.held.adapters.FeedAdapter;
import com.held.customview.BlurTransformation;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    private boolean isLastPage, isLoading;
    private List<FeedData> mFeedList = new ArrayList<>();
    private int mLimit = 5;
    private long mStart = System.currentTimeMillis();
    private ImageView mFullImg;

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

        Log.d(TAG, " PIN " + PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)));
        mFullImg = (ImageView) view.findViewById(R.id.FEED_full_img);
        mFeedRecyclerView = (RecyclerView) view.findViewById(R.id.FEED_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mFeedRecyclerView.setLayoutManager(mLayoutManager);
        mFeedResponse = new FeedResponse();
        blurTransformation = new BlurTransformation(getCurrActivity(), 25f);
        mFeedAdapter = new FeedAdapter((FeedActivity) getCurrActivity(), mFeedList, blurTransformation, isLastPage, this);
        mFeedRecyclerView.setAdapter(mFeedAdapter);
        if (getCurrActivity().getNetworkStatus()) {
//            DialogUtils.showProgressBar();
            callFeedApi();
        } else
            UiUtils.showSnackbarToast(getView(), "Sorry! You don't seem to connected to internet");
        mSearchEdt = (EditText) getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_search_edt);

        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (getCurrActivity().getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callUserSearchApi();
                    } else {
                        UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                    }
                    return true;
                }
                return false;
            }
        });
       /* mSearchEdt.addTextChangedListener(new TextWatcher() {
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
        });*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FEED_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getCurrActivity().getNetworkStatus()) {
                    isLastPage = false;
                    mFeedList.clear();
                    mStart = System.currentTimeMillis();
//                    DialogUtils.showProgressBar();
                    callFeedApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mFeedRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!isLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !isLoading) {
                    callFeedApi();
                }
            }
        });
        Utils.hideSoftKeyboard(getCurrActivity());
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (getView() != null) {
            getView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    private void showSystemUI() {
        if (getView() != null) {
            getView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public void showFullImg(String url) {
        mFullImg.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        getCurrActivity().getToolbar().setVisibility(View.GONE);
        Picasso.with(getActivity()).load(url).into(mFullImg);
        mSwipeRefreshLayout.setEnabled(false);
        toggleHideyBar();
    }

    public void showRCView() {
        mFullImg.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        getCurrActivity().getToolbar().setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(true);
        toggleHideyBar();
    }

    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }


    private void callUserSearchApi() {
        HeldService.getService().searchUser(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                mSearchEdt.getText().toString().trim(), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        Utils.hideSoftKeyboard(getCurrActivity());
                        Bundle bundle = new Bundle();
                        bundle.putString("name", searchUserResponse.getDisplay_name());
                        bundle.putString("image", searchUserResponse.getPic());
                        getCurrActivity().perform(5, bundle);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
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
        isLoading = true;
        if (getCurrActivity().getNetworkStatus()) {//PreferenceHelper.getInstance(getCurrActivity()).readPreference("SESSION_TOKEN")
            HeldService.getService().feedPostWithPage(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                    mLimit, mStart, new Callback<FeedResponse>() {
                        @Override
                        public void success(FeedResponse feedResponse, Response response) {
//                            DialogUtils.stopProgressDialog();
                            mFeedResponse = feedResponse;
                            mFeedList.addAll(mFeedResponse.getObjects());
                            isLastPage = mFeedResponse.isLastPage();
                            mFeedAdapter.setFeedResponse(mFeedList, isLastPage);
                            mStart = mFeedResponse.getNextPageStart();
                            isLoading = false;
                        }

                        @Override
                        public void failure(RetrofitError error) {
//                            DialogUtils.stopProgressDialog();
                            isLoading = false;
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
    }

    @Override
    protected void bindListeners(View view) {
    }

    @Override
    public void onClicked(View v) {
    }

}
