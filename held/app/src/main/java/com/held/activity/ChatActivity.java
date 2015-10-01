package com.held.activity;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.FriendsListFragment;
import com.held.utils.AppConstants;
import com.held.utils.Utils;

public class ChatActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;
    private static ChatActivity activity;
    private final String TAG = "ChatActivity";

    public static ChatActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting Chat activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity = this;
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
      //  mSearchEdt = (EditText) findViewById(R.id.TOOLBAR_search_edt);
     //   mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
     //   mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
       // mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mChat.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
//        mRetakeBtn.setOnClickListener(this);
  //      mPostBtn.setOnClickListener(this);
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
        mCamera.setImageResource(R.drawable.icon_feed);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");

        mSearchEdt.setVisibility(View.INVISIBLE);
        mUsername.setVisibility(View.VISIBLE);
        mChat.setImageResource(R.drawable.back);
        mCamera.setImageResource(R.drawable.icon_menu);
        if (!isOneToOne) {
            mUsername.setText("Held");
        } else {
            mUsername.setText("@" + id);
        }

        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    private void launchChatScreenFromInbox(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");

        mSearchEdt.setVisibility(View.INVISIBLE);
        mUsername.setVisibility(View.VISIBLE);
        mCamera.setImageResource(R.drawable.icon_menu);
        mChat.setImageResource(R.drawable.back);
        if (!isOneToOne) {
            mUsername.setText("Held");
        } else {
            mUsername.setText("@" + id);
        }
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG, true);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof ChatFragment) {
            super.onBackPressed();
            mSearchEdt.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.INVISIBLE);
            mCamera.setImageResource(R.drawable.icon_feed);
            mChat.setImageResource(R.drawable.icon_chat);
            mDisplayFragment = Utils.getCurrVisibleFragment(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreenFromInbox(bundle.getString("owner_displayname"), true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;


            case R.id.toolbar_post_img:
                launchCreatePostScreen();
                break;

            case R.id.toolbar_chat_img:
                if (mDisplayFragment instanceof ChatFragment) {
                    onBackPressed();
                }

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
