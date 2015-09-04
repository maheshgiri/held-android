package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.FeedFragment;
import com.held.fragment.HomeFragment;
import com.held.fragment.ProfileFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;

public class FeedActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("isProfile")) {
                launchProfileScreen(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_name)),
                        PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_img)));
            }
        } else {
//            launchFeedScreen();
            launchHomeScreen();
        }

        mChat = (ImageView) findViewById(R.id.TOOLBAR_chat_img);
        mCamera = (ImageView) findViewById(R.id.TOOLBAR_camera_img);
        mNotification = (ImageView) findViewById(R.id.TOOLBAR_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.TOOLBAR_search_edt);
        mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
        mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
        mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        mChat.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mRetakeBtn.setOnClickListener(this);
        mPostBtn.setOnClickListener(this);


    }

    private void launchFeedScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FeedFragment.newInstance(), FeedFragment.TAG, true);
        mDisplayFragment = FeedFragment.newInstance();
    }

    private void launchHomeScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(HomeFragment.newInstance(), HomeFragment.TAG, true);
        mDisplayFragment = FeedFragment.newInstance();
    }

    private void launchCreatePostScreen() {
        Intent intent = new Intent(FeedActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        Intent intent = new Intent(FeedActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        startActivity(intent);
    }

    private void launchChatListScreen() {
        Intent intent = new Intent(FeedActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(FeedActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    private void launchRequestFriendScreen(String name, String image) {
        updateToolbar(false, false, false, false, false, false, false, "");
        addFragment(SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image), SendFriendRequestFragment.TAG, true);
        mDisplayFragment = SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image);
    }


    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case AppConstants.LAUNCH_POST_SCREEN:
                launchCreatePostScreen();
                break;
            case AppConstants.LAUNCH_FEED_SCREEN:
                launchHomeScreen();
                break;
            case AppConstants.LAUNCH_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("postid"), false);
                break;
            case AppConstants.LAUNCH_NOTIFICATION_SCREEN:
                launchNotificationScreen();
                break;
            case AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("owner_displayname"), true);
                break;
            case AppConstants.LAUNCH_PROFILE_SCREEN:
                if (bundle != null)
                    launchProfileScreen(bundle.getString("uid"), bundle.getString("userImg"));
                break;

        }
    }

    private void launchProfileScreen(String uid, String userImg) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ProfileFragment.newInstance(uid, userImg), ProfileFragment.TAG, true);
        mDisplayFragment = ProfileFragment.newInstance(uid, userImg);
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof HomeFragment)
            finish();
        else {
            super.onBackPressed();
            updateToolbar(true, false, true, false, true, true, false, "");
            mDisplayFragment = Utils.getCurrVisibleFragment(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TOOLBAR_chat_img:
                launchChatListScreen();
                break;
            case R.id.TOOLBAR_notification_img:
                perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                break;
            case R.id.TOOLBAR_camera_img:
                perform(AppConstants.LAUNCH_POST_SCREEN, null);
                break;

        }
    }

    public void onLeftSwipe() {
        // Do something
        launchCreatePostScreen();

    }

    public void onRightSwipe() {
        // Do something
        launchChatListScreen();
    }

    public void updateToolbarForPost() {
        mRetakeBtn.setVisibility(View.VISIBLE);
        mPostBtn.setVisibility(View.VISIBLE);
        mCamera.setVisibility(View.GONE);
        mNotification.setVisibility(View.GONE);
        mChat.setVisibility(View.GONE);
        mSearchEdt.setVisibility(View.INVISIBLE);
        mUsername.setVisibility(View.INVISIBLE);
    }

    public void updateToolbar(){
        mRetakeBtn.setVisibility(View.GONE);
        mPostBtn.setVisibility(View.GONE);
        mCamera.setVisibility(View.VISIBLE);
        mNotification.setVisibility(View.VISIBLE);
        mChat.setVisibility(View.VISIBLE);
        mSearchEdt.setVisibility(View.VISIBLE);
        mUsername.setVisibility(View.INVISIBLE);
    }

}
