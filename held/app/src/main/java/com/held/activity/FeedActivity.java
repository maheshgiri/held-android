package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.FeedFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.utils.AppConstants;

public class FeedActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

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

        launchFeedScreen();
    }

    private void launchFeedScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FeedFragment.newInstance(), FeedFragment.TAG, true);
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
            case 0:
                launchCreatePostScreen();
                break;
            case 1:
                launchFeedScreen();
                break;
            case 2:
                if (bundle != null)
                    launchChatScreen(bundle.getString("postid"), false);
                break;
            case 3:
//                launchCreatePostFragmentFromFeed();
                break;
            case 4:
                launchNotificationScreen();
                break;
            case 5:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case 6:
                if (bundle != null)
                    launchChatScreen(bundle.getString("owner_displayname"), true);
                break;
            case 7:
//                launchInboxPage();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof FeedFragment)
            finish();
        else {
            super.onBackPressed();
            updateToolbar(true, false, true, false, true, true, false, "");
            mDisplayFragment = FeedFragment.newInstance();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TOOLBAR_chat_img:
                launchChatListScreen();
                break;
            case R.id.TOOLBAR_notification_img:
                perform(4, null);
                break;
            case R.id.TOOLBAR_camera_img:
                perform(0, null);
                break;
        }
    }
}
