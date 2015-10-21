package com.held.activity;

import android.graphics.Typeface;
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

import com.held.fragment.NotificationFragment;

import timber.log.Timber;

public class NotificationActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private final String TAG = "NotificationActivity";
    private TextView mUsername,mTitle;
    private boolean firstClick=true;
    private String mUserNameForSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting notification activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearch=(ImageView) findViewById(R.id.toolbar_search_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
       // mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
     //   mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
       // mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        mChat.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mSearch.setOnClickListener(this);

//        mRetakeBtn.setOnClickListener(this);
//        mPostBtn.setOnClickListener(this);
        mChat.setVisibility(View.GONE);
        mCamera.setImageResource(R.drawable.home);
        mSearchEdt.setVisibility(View.GONE);
        mTitle.setText("Notifications");
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);

        mSearchEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearch.setVisibility(View.VISIBLE);
                    mTitle.setVisibility(View.GONE);
                } else {
                    mSearch.setVisibility(View.VISIBLE);
                    mTitle.setVisibility(View.VISIBLE);
                    mSearchEdt.setVisibility(View.GONE);
                }
            }
        });
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
            case R.id.toolbar_chat_img:
                launchChatListScreen();
                break;
            case R.id.toolbar_post_img:
                launchFeedScreen();
                break;
            case R.id.toolbar_search_img:
                Log.d(TAG, "toolbar search image has been clicked");
                if(firstClick) {
                    visibleTextView();
                    firstClick=false;
                }
                else {
                    mUserNameForSearch= mSearchEdt.getText().toString();
                    Timber.i("User Name for search :" + mUserNameForSearch);
                    mSearchEdt.setText("");
                    hideTextView();
                    firstClick=true;
                    launchSearchScreen(mUserNameForSearch);

                }

                break;
        }
    }

    private void launchFeedScreen() {
        Intent intent = new Intent(NotificationActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    private void launchChatListScreen() {
        Intent intent = new Intent(NotificationActivity.this, InboxActivity.class);
        startActivity(intent);
    }
    public void visibleTextView(){

        mSearchEdt.setVisibility(View.VISIBLE);
        mSearchEdt.setFocusable(true);
        mSearchEdt.setFocusableInTouchMode(true);
        mSearchEdt.requestFocus();
        mTitle.setVisibility(View.GONE);


    }
    public void hideTextView(){

        mSearchEdt.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
    }
    private void launchSearchScreen(String uname) {
        Intent intent = new Intent(NotificationActivity.this, SearchActivity.class);
        intent.putExtra("userName", uname);
        startActivity(intent);
    }
}

