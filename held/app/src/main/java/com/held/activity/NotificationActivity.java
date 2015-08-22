package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.NotificationFragment;

public class NotificationActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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
            launchNotificationScreen(getIntent().getExtras().getInt("id"));
        } else {
            launchNotificationScreen();
        }
    }

    private void launchNotificationScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(), NotificationFragment.TAG);
        mDisplayFragment = NotificationFragment.newInstance();
    }

    private void launchNotificationScreen(int id) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(id), NotificationFragment.TAG);
        mDisplayFragment = NotificationFragment.newInstance(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TOOLBAR_chat_img:
                launchChatListScreen();
                break;
            case R.id.TOOLBAR_camera_img:
                launchCreatePostScreen();
                break;
        }
    }

    private void launchCreatePostScreen() {
        Intent intent = new Intent(NotificationActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void launchChatListScreen() {
        Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}

