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

import timber.log.Timber;

public class InboxActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;
    private static InboxActivity activity;
    private final String TAG = "InboxActivity";

    public static InboxActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting Chat activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity = this;
        TextView title = (TextView)findViewById(R.id.toolbar_title_txt);
        title.setText("Inbox");
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setVisibility(View.GONE);
     //   mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
     //   mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
       // mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        mCamera.setImageResource(R.drawable.home_icon_v2);
        mCamera.setVisibility(View.VISIBLE);
        //mCamera.setImageDrawable(homeIcon);
        mChat.setVisibility(View.GONE);

        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
//        mRetakeBtn.setOnClickListener(this);
  //      mPostBtn.setOnClickListener(this);
        if (getIntent().getExtras() != null) {

            String chatid = getIntent().getExtras().getString("id");
            Boolean isOneToOne = getIntent().getExtras().getBoolean("isOneToOne");
            launchChatScreen( chatid, isOneToOne);
        } else {
            Log.d(TAG, "Launching inbox");
            launchInboxPage();
        }
    }

    private void launchInboxPage() {
        //updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FriendsListFragment.newInstance(), FriendsListFragment.TAG);
//        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out,
//                R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        //mCamera.setImageResource(R.drawable.icon_feed);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");



      /*  if (!isOneToOne) {
            mUsername.setText("Held");
        } else {
            mUsername.setText("@" + id);
        }*/

        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    private void launchChatScreenFromInbox(String id, boolean isOneToOne) {
        Timber.d("Launching chatscreen with user id " + id + " isonetoone " + isOneToOne);
        Intent intent = new Intent(InboxActivity.this, ChatActivity.class);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("chatId", id);
        startActivity(intent);
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof ChatFragment) {
            Log.d(TAG, "on back pressed. current fragment is chat fragment");
            super.onBackPressed();
            mSearchEdt.setVisibility(View.GONE);
//            mUsername.setVisibility(View.INVISIBLE);
            mCamera.setImageResource(R.drawable.icon_feed);
            mChat.setImageResource(R.drawable.icon_chat);
            mDisplayFragment = Utils.getCurrVisibleFragment(this);
        } else {
            Log.d(TAG, "unknown current fragment");
            super.onBackPressed();
        }
    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                if (bundle != null)

                    launchChatScreenFromInbox(bundle.getString("user_id"),true);
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
        Intent intent = new Intent(InboxActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(InboxActivity.this, NotificationActivity.class);
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