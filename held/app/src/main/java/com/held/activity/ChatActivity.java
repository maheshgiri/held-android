package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.FriendsListFragment;

public class ChatActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;
    private static ChatActivity activity;

    public static ChatActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity = this;
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
        if (getIntent().getExtras() != null) {
            launchChatScreen(getIntent().getExtras().getString("id"), getIntent().getExtras().getBoolean("isOneToOne"));
        } else {
            launchInboxPage();
        }
    }

    private void launchInboxPage() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FriendsListFragment.newInstance(), FriendsListFragment.TAG);
//        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out,
//                R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    private void launchChatScreenFromInbox(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        mChat.setImageResource(R.drawable.icon_back);
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG, true);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case 0:
                if (bundle != null)
                    launchChatScreenFromInbox(bundle.getString("owner_displayname"), true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TOOLBAR_notification_img:
                launchNotificationScreen();
                break;
            case R.id.TOOLBAR_camera_img:
                launchCreatePostScreen();
                break;
            case R.id.TOOLBAR_chat_img:
                onBackPressed();
                break;
        }
    }

    private void launchCreatePostScreen() {
        Intent intent = new Intent(ChatActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(ChatActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    public void onLeftSwipe() {
        // Do something
        finish();
    }

    public void onRightSwipe() {
        // Do something
        launchNotificationScreen();
    }
}
