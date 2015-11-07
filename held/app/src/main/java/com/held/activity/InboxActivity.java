package com.held.activity;

import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
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
    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername,title;
    private static InboxActivity activity;
    private final String TAG = "InboxActivity";
    private final boolean flag=true;
    private boolean firstClick=true;
    private String mUserNameForSearch;
    View statusBar;
    public static InboxActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting Chat activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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
        activity = this;
        title = (TextView)findViewById(R.id.toolbar_title_txt);
        title.setText("Inbox");
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        title.setTypeface(medium);

        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setVisibility(View.GONE);
        mSearch=(ImageView) findViewById(R.id.toolbar_search_img);
     //   mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
     //   mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
       // mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        mCamera.setImageResource(R.drawable.home);
        mCamera.setVisibility(View.VISIBLE);
        //mCamera.setImageDrawable(homeIcon);
        mChat.setVisibility(View.GONE);

        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mSearch.setOnClickListener(this);
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
        mSearchEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearch.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearch.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    mSearchEdt.setVisibility(View.GONE);
                }
            }
        });
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
/////add chat back image for personal chat
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
                launchFeedScreen();
                break;

            case R.id.toolbar_chat_img:
                if (mDisplayFragment instanceof ChatFragment) {
                    onBackPressed();
                }

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
        Intent intent = new Intent(InboxActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(InboxActivity.this, NotificationActivity.class);
        startActivity(intent);
    }
    public void visibleTextView(){

        mSearchEdt.setVisibility(View.VISIBLE);
        mSearchEdt.setFocusable(true);
        mSearchEdt.setFocusableInTouchMode(true);
        mSearchEdt.requestFocus();
        title.setVisibility(View.GONE);


    }
    public void hideTextView(){

        mSearchEdt.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
    }
    private void launchSearchScreen(String uname) {
        Intent intent = new Intent(InboxActivity.this, SearchActivity.class);
        intent.putExtra("userName", uname);
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
