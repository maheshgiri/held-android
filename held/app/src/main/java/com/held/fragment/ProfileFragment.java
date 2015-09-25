package com.held.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
<<<<<<< HEAD
import android.util.Log;
=======
import android.view.KeyEvent;
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.FeedActivity;
import com.held.activity.R;
import com.held.adapters.ProfileAdapter;
import com.held.customview.PicassoCache;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.SearchUserResponse;
<<<<<<< HEAD
import com.held.utils.AppConstants;
=======
import com.held.utils.DialogUtils;
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFragment extends ParentFragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<FeedData> mPostList = new ArrayList<>();
    private ProfileAdapter mProfileAdapter;
    private boolean mIsLastPage, mIsLoading = true;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 5;
    private SwipeRefreshLayout mSwipeRefreshLayout;
<<<<<<< HEAD
    private String mUid, mUserName,user_id;
    private PreferenceHelper mPreference;
    private ImageView mFullImg,mUserImg;
    private FeedActivity mActivity;
=======
    private String mUid, mUserName, mUserImg = "";
    private ImageView mFullImg;
    private EditText mSearchEdt;


>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
    public static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView mProfilePic;
    private TextView mUserNameText;

    public static ProfileFragment newInstance(String uid, String userImg) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        bundle.putString("userImg", userImg);
        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.PROFILE_rc_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mProfileAdapter = new ProfileAdapter(getCurrActivity(), mPostList, mIsLastPage, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mProfileAdapter);
        mFullImg = (ImageView) view.findViewById(R.id.PROFILE_full_img);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.PROFILE_swipe_refresh_layout);
<<<<<<< HEAD
        //mUserName = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_user_name));
        mUserImg=(ImageView)view.findViewById(R.id.profile_img);
        mProfilePic=(ImageView)view.findViewById(R.id.PROFILE_pic);
        mPreference=PreferenceHelper.getInstance(getCurrActivity());
       // mUserNameText=(TextView)view.findViewById(R.id.PROFILE_name);
       // mUserNameText.setText(mUserName);
        //loadProfile();
=======
        mSearchEdt = (EditText) getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_search_edt);

        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideSoftKeyboard(getCurrActivity());
                    return true;
                }
                return false;
            }
        });

        if (getArguments() != null) {
            mUserName = getArguments().getString("uid");
            mUserImg = getArguments().getString("userImg");
        } else {
            mUserName = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_user_name));
            mUserImg = "http://139.162.1.137/api/user_images/tejasshah_1440819300949.jpg";//PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_user_img));
        }

>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCount && !mIsLoading) {
                    callProfilePostAPi();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mStart = System.currentTimeMillis();
                mPostList.clear();
                mIsLastPage = false;
                if (getCurrActivity().getNetworkStatus()) {
                    callProfilePostAPi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
            }
        });

        if (getCurrActivity().getNetworkStatus()) {
            callUserSearchApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
        }
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                mPreference.readPreference(getString(R.string.API_user_regId)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        user_id = searchUserResponse.getRid();
                        callProfilePostAPi();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (getView() != null) {
            getView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    private void showSystemUI() {
        if (getView() != null) {
            getView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public void showFullImg(String url) {
        getCurrActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getCurrActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mFullImg.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        getCurrActivity().getToolbar().setVisibility(View.GONE);
        Picasso.with(getActivity()).load(url).into(mFullImg);
        mSwipeRefreshLayout.setEnabled(false);
        hideSystemUI();
    }

    public void showRCView() {
        getCurrActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getCurrActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFullImg.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        getCurrActivity().getToolbar().setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(true);
        showSystemUI();
    }

    private void callProfilePostAPi() {
        mIsLoading = true;
        HeldService.getService().getUserPosts(mPreference.readPreference(getString(R.string.API_session_token)),
                mPreference.readPreference(getString(R.string.API_user_regId)), mStart, mLimit,
                new Callback<FeedResponse>() {
                    @Override
                    public void success(FeedResponse feedResponse, Response response) {
                        mPostList.addAll(feedResponse.getObjects());
                        mIsLastPage = feedResponse.isLastPage();
                        mStart = feedResponse.getNextPageStart();
                        mProfileAdapter.setPostList(mPostList, mIsLastPage);
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
<<<<<<< HEAD
    public void loadProfile()
    {
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                mPreference.readPreference(getString(R.string.API_user_regId)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        Log.i("PostFragment", "@@Image Url" + searchUserResponse.getProfilePic());
                        PicassoCache.getPicassoInstance(mActivity)
                                .load(AppConstants.BASE_URL+searchUserResponse.getProfilePic())
                                .placeholder(R.drawable.user_icon)
                                .into(mProfilePic);
                        mUserNameText.setText(searchUserResponse.getDisplayName());
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

=======

    public String getUserName() {
        return mUserName;
    }

    public String getUserImg() {
        return mUserImg;
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
    }
}
