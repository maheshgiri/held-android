package com.held.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.ParentFragment;
import com.held.utils.AppConstants;
import com.held.utils.Utils;
import timber.log.Timber;

/**
 * Created by swapnil on 3/10/15.
 */
public class ChatActivity extends ParentActivity implements View.OnClickListener{

    ImageView mChat, mCamera, mNotification;
    EditText mSearchEdt;
    Activity mActivity;
    Fragment mDisplayFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mActivity = this;
        TextView title = (TextView)findViewById(R.id.toolbar_title_txt);
        title.setText("");
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setVisibility(View.GONE);
        mChat.setImageResource(R.drawable.back);
        mCamera.setImageResource(R.drawable.icon_menu);
        mCamera.setVisibility(View.VISIBLE);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        boolean isOneToOne = extras.getBoolean("isOneToOne");
        String chatId = extras.getString("chatId");
        Timber.d("Chat activity received chat id " + chatId + " isontotone: " + isOneToOne);
        launchChatScreen(chatId, isOneToOne);
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        ParentFragment frag = ChatFragment.newInstance(id, isOneToOne);
        addFragment( frag, ChatFragment.TAG);
        mDisplayFragment = frag;
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof ChatFragment) {
            Timber.d("on back pressed. current fragment is chat fragment");
            super.onBackPressed();
            mSearchEdt.setVisibility(View.GONE);
//            mUsername.setVisibility(View.INVISIBLE);
            mCamera.setImageResource(R.drawable.icon_feed);
            mChat.setImageResource(R.drawable.icon_chat);
            mDisplayFragment = Utils.getCurrVisibleFragment(this);
        } else {
            Timber.d("unknown current fragment");
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;


            case R.id.toolbar_post_img:
                // todo:
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





}
