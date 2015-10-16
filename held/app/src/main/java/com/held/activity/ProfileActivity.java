package com.held.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.R;
import com.held.fragment.ChatFragment;
import com.held.fragment.ParentFragment;
import com.held.fragment.ProfileFragment;

import timber.log.Timber;

public class ProfileActivity extends ParentActivity implements View.OnClickListener {

    ImageView mChat, mCamera, mNotification;
    EditText mSearchEdt;
    Activity mActivity;
    Fragment mDisplayFragment;
    String mUserId;
    ProfileFragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mActivity = this;
        TextView title = (TextView)findViewById(R.id.toolbar_title_txt);
        title.setText("Profile");
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
        if (mDisplayFragment instanceof ProfileFragment){
            super.onBackPressed();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
