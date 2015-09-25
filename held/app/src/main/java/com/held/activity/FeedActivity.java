package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import com.held.fragment.FeedFragment;
import com.held.fragment.ProfileFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;

public class FeedActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat_img, mPost_img, mNotification_img,mSearch_img;
    private EditText mSearch_edt;
    private TextView mTitle_txt;
    private GestureDetector gestureDetector;
    private Toolbar mHeld_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
//        mHeld_toolbar=(Toolbar)findViewById(R.id.toolbar_main);
       if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("isProfile")) {
                launchProfileScreen(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_name)));
            }
        } else {
            launchFeedScreen();
        }

        setToolbar();
        launchFeedScreen();
        mChat_img=(ImageView)findViewById(R.id.toolbar_chat_img);
        mSearch_img=(ImageView)findViewById(R.id.toolbar_search_img);
        mNotification_img=(ImageView)findViewById(R.id.toolbar_notification_img);
        mPost_img=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle_txt=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);

        mChat_img.setOnClickListener(this);
        mSearch_img.setOnClickListener(this);
        mNotification_img.setOnClickListener(this);
        mPost_img.setOnClickListener(this);
        mSearch_edt.setVisibility(View.GONE);



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
                launchChatListScreen();
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
                launchNotificationScreen();
                break;
            case 8:
                if (bundle != null)
                    launchProfileScreen(bundle.getString("uid"));
                break;

        }
    }

    private void launchProfileScreen(String uid) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ProfileFragment.newInstance(uid), ProfileFragment.TAG, true);
        mDisplayFragment = ProfileFragment.newInstance(uid);
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof FeedFragment)
            finish();
        else if (mDisplayFragment instanceof ProfileFragment)
            launchFeedScreen();
        else {
            super.onBackPressed();
            updateToolbar(true, false, true, false, true, true, false, "");
            mDisplayFragment = FeedFragment.newInstance();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_chat_img:
                perform(4,null);
                break;
            case R.id.toolbar_notification_img:
                perform(7,null);
                break;
            case R.id.toolbar_post_img:
                perform(0, null);
                break;
            case R.id.toolbar_search_img:
                visibleTextView();
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

    public void callSerachFriendApi()
    {

    }
    public void visibleTextView(){

        mSearch_edt.setVisibility(View.VISIBLE);
        mTitle_txt.setVisibility(View.GONE);


    }
}
