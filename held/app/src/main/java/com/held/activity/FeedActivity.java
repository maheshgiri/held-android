package com.held.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView mChat_img, mPost_img, mNotification_img,mSearch_img;
    private EditText mSearch_edt;
    private TextView mTitle_txt;
    private GestureDetector gestureDetector;
<<<<<<< HEAD
    private Toolbar mHeld_toolbar;

=======
    private int mPosition = 1;

    private RelativeLayout mPosttoolbar;
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88


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
<<<<<<< HEAD
            case 3:
//                launchCreatePostFragmentFromFeed();
                break;
            case 4:
                launchChatListScreen();
=======
            case AppConstants.LAUNCH_NOTIFICATION_SCREEN:
                launchNotificationScreen();
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
                break;
            case AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("owner_displayname"), true);
                break;
<<<<<<< HEAD
            case 7:
                launchNotificationScreen();
                break;
            case 8:
=======
            case AppConstants.LAUNCH_PROFILE_SCREEN:
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
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
<<<<<<< HEAD
            case R.id.toolbar_chat_img:
                perform(4,null);
                break;
            case R.id.toolbar_notification_img:
                perform(7,null);
                break;
            case R.id.toolbar_post_img:
                perform(0, null);
=======
            case R.id.TOOLBAR_chat_img:
                if (mPosition == 0) {

                } else if (mPosition == 1) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(0);
                } else if (mPosition == 2) {

                } else if (mPosition == 3) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(2);
                }
                break;
            case R.id.TOOLBAR_notification_img:
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 2) {

                } else if (mPosition == 3) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                }
                break;
            case R.id.TOOLBAR_camera_img:
                if (mPosition == 0) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(1);
                } else if (mPosition == 1) {
                    ((HomeFragment) mDisplayedFragment).updateViewPager(2);
                } else if (mPosition == 2) {

                } else if (mPosition == 3) {
//                    ((HomeFragment) mDisplayedFragment).updateViewPager(2);
                }
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
                break;
            case R.id.toolbar_search_img:
                visibleTextView();
                break;
        }
    }

<<<<<<< HEAD


=======
    public void updateViewPager(int position) {
        mPosition = position;
        updateToolbar();
    }
>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88

    public void onLeftSwipe() {
        // Do something
        launchCreatePostScreen();
    }

    public void onRightSwipe() {
        // Do something
        launchChatListScreen();
    }

<<<<<<< HEAD
    public void callSerachFriendApi()
    {

    }
    public void visibleTextView(){

        mSearch_edt.setVisibility(View.VISIBLE);
        mTitle_txt.setVisibility(View.GONE);


    }
=======
    public void updateToolbar() {
        if (mPosition == 0) {
            mChat.setImageResource(R.drawable.icon_back);
            mCamera.setImageResource(R.drawable.icon_feed);
        } else if (mPosition == 1) {
            mRetakeBtn.setVisibility(View.GONE);
            mPostBtn.setVisibility(View.GONE);
            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearchEdt.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.INVISIBLE);
            mChat.setImageResource(R.drawable.icon_chat);
            mCamera.setImageResource(R.drawable.icon_camera);
        } else if (mPosition == 2) {
            mRetakeBtn.setVisibility(View.VISIBLE);
            mPostBtn.setVisibility(View.VISIBLE);
            mCamera.setVisibility(View.GONE);
            mNotification.setVisibility(View.GONE);
            mChat.setVisibility(View.GONE);
            mSearchEdt.setVisibility(View.INVISIBLE);
            mUsername.setVisibility(View.INVISIBLE);
        } else if (mPosition == 3) {
            mRetakeBtn.setVisibility(View.GONE);
            mPostBtn.setVisibility(View.GONE);
            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearchEdt.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.INVISIBLE);
            mChat.setImageResource(R.drawable.icon_camera);
            mCamera.setImageResource(R.drawable.icon_feed);

        }
    }

>>>>>>> c73f45de2d83aa7516b8ca26b78bd6aa11e0fb88
}
