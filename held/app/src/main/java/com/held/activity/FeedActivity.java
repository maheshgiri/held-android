package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import com.held.fragment.FeedFragment;
import com.held.fragment.HomeFragment;
import com.held.fragment.ProfileFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;

public class FeedActivity extends ParentActivity implements View.OnClickListener {

    //    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearch_edt;
    private TextView mTitle;
    private GestureDetector gestureDetector;
    private Toolbar mHeld_toolbar;
    private int mPosition = 1;

    private RelativeLayout mPosttoolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
//        mHeld_toolbar=(Toolbar)findViewById(R.id.toolbar_main);
       if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("isProfile")) {
                launchProfileScreen(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_name)),
                        PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_img)));
            }
        } else {

//            launchFeedScreen();
            launchHomeScreen();
        }


        setToolbar();
        launchFeedScreen();
        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mSearch=(ImageView)findViewById(R.id.toolbar_search_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);

        mChat.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mSearch_edt.setVisibility(View.GONE);



    }

    private void launchFeedScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FeedFragment.newInstance(), FeedFragment.TAG, true);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
    }

    private void launchHomeScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(HomeFragment.newInstance(), HomeFragment.TAG, true);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
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
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
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
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
    }

    @Override
    public void onBackPressed() {
        if (mDisplayedFragment instanceof HomeFragment)
            finish();
        else {
            super.onBackPressed();
            updateToolbar(true, false, true, false, true, true, false, "");
            mDisplayedFragment = Utils.getCurrVisibleFragment(this);
        }
    }

    @Override
    public void onClick(View view) {
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
//        if(mDisplayedFragment==null)
        switch (view.getId()) {


            case R.id.toolbar_chat_img:

                if (mPosition == 0) {

                } else if (mPosition == 1) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(0);
                } else if (mPosition == 2) {

                } else if (mPosition == 3) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(2);
                }
                break;
            case R.id.toolbar_notification_img:
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 2) {

                } else if (mPosition == 3) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                }
                break;
            case R.id.toolbar_post_img:
                if (mPosition == 0) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(1);
                } else if (mPosition == 1) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(2);
                } else if (mPosition == 2) {

                } else if (mPosition == 3) {
//                    ((HomeFragment) mDisplayedFragment).updateViewPager(2);
                }

                break;
            case R.id.toolbar_search_img:
                visibleTextView();
                break;
        }
    }
    public void updateViewPager(int position) {
        mPosition = position;
        updateToolbar();
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
        mTitle.setVisibility(View.GONE);


    }

    public void updateToolbar() {
        if (mPosition == 0) {
            mChat.setImageResource(R.drawable.icon_back);
            mCamera.setImageResource(R.drawable.icon_feed);
        } else if (mPosition == 1) {

          //  mRetakeBtn.setVisibility(View.GONE);
         //   mPostBtn.setVisibility(View.GONE);
            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearch_edt.setVisibility(View.VISIBLE);
         //   mUsername.setVisibility(View.INVISIBLE);
            mChat.setImageResource(R.drawable.icon_chat);
            mCamera.setImageResource(R.drawable.icon_camera);
        } else if (mPosition == 2) {
          //  mRetakeBtn.setVisibility(View.VISIBLE);
          //  mPostBtn.setVisibility(View.VISIBLE);
            mCamera.setVisibility(View.GONE);
            mNotification.setVisibility(View.GONE);
            mChat.setVisibility(View.GONE);
            mSearch_edt.setVisibility(View.INVISIBLE);
           // mUsername.setVisibility(View.INVISIBLE);
        } else if (mPosition == 3) {
           // mRetakeBtn.setVisibility(View.GONE);
           // mPostBtn.setVisibility(View.GONE);
            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearch_edt.setVisibility(View.VISIBLE);
           // mUsername.setVisibility(View.INVISIBLE);
            mChat.setImageResource(R.drawable.icon_camera);
            mCamera.setImageResource(R.drawable.icon_feed);


        }
    }


}
