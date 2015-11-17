package com.held.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ParentFragment;
import com.held.fragment.ProfileFragment;

public class ProfileActivity extends ParentActivity implements View.OnClickListener {

    ImageView mChat, mCamera, mNotification,mSearch;
    EditText mSearchEdt;
    private TextView mTitle;
    Activity mActivity;
    Fragment mDisplayFragment;
    String mUserId;
    ProfileFragment frag;
    private Toolbar mHeld_toolbar;
    private View toolbar_divider;
    private boolean firstClick=true;
    private String mUserNameForSearch;
    private final String TAG = "ProfileActivity";
    View statusBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mHeld_toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mHeld_toolbar);

        statusBar=(View)findViewById(R.id.statusBarView);
        Window w = getWindow();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            statusBar.setVisibility(View.VISIBLE);

        }else {
            statusBar.setVisibility(View.GONE);
        }
        mActivity = this;

        //setToolbar();
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mTitle.setText("Profile");
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setVisibility(View.GONE);
        mSearch=(ImageView)findViewById(R.id.toolbar_search_img);
        mChat.setImageResource(R.drawable.camera);
        mCamera.setImageResource(R.drawable.menu);
        mCamera.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.GONE);
        mSearchEdt.setVisibility(View.GONE);
        toolbar_divider=(View)findViewById(R.id.toolbar_divider);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mChat.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        mUserId=extras.getString("user_id");
        launchProfileScreen(mUserId);
    }

    private void launchProfileScreen(String uid) {
        ParentFragment frag = ProfileFragment.newInstance(uid);
        Bundle bundle=new Bundle();
        bundle.putString("user_id", uid);
        frag.setArguments(bundle);
        addFragment(ProfileFragment.newInstance(uid), ProfileFragment.TAG, true);
        mDisplayedFragment = ProfileFragment.newInstance(uid);
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }
    @Override
    public void onBackPressed() {
        getCurrentFragment();
        if (mDisplayedFragment instanceof ProfileFragment && mDisplayedFragment.isVisible()){
            super.onBackPressed();
            this.finishActivity(Activity.RESULT_OK);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_chat_img:
//                if (mDisplayFragment instanceof ProfileFragment) {
                    //onBackPressed();
                launchCreatePostScreen();

                break;
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;

        }

    }
    public void hideToolbar(){
        mHeld_toolbar.setVisibility(View.GONE);
        toolbar_divider.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.GONE);}
    }

    public void showToolbar(){
        mHeld_toolbar.setVisibility(View.VISIBLE);
        toolbar_divider.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.VISIBLE);}
    }
    private void launchNotificationScreen() {
        Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
        startActivity(intent);
    }
    public void visibleTextView(){

        mSearchEdt.setVisibility(View.VISIBLE);
        mSearchEdt.setFocusable(true);
        mSearchEdt.setFocusableInTouchMode(true);
        mSearchEdt.requestFocus();
        mTitle.setVisibility(View.GONE);
        mSearch.setVisibility(View.VISIBLE);

    }
    public void hideTextView(){

        mSearchEdt.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
    }
    private void launchSearchScreen(String uname) {
        Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
        intent.putExtra("userName", uname);
        startActivity(intent);
    }
    private void launchCreatePostScreen() {
        Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
        startActivity(intent);
    }
}
