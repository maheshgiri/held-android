package com.held.activity;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.held.fragment.ChatFragment;
import com.held.fragment.FeedFragment;
import com.held.fragment.HomeFragment;
import com.held.fragment.ProfileFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FeedActivity extends ParentActivity implements View.OnClickListener {

    //    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearch_edt;
    private TextView mTitle;
    private GestureDetector gestureDetector;
    private Toolbar mHeld_toolbar;
    private final String TAG = "FeedActivity";
    private RelativeLayout mPosttoolbar;
    private int mPosition = 1;
    private PreferenceHelper mPreference;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting feed activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

       if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("isProfile")) {
                launchProfileScreen(mPreference.readPreference(getString(R.string.API_user_name)));
            }
        } else {
            launchFeedScreen();
//            launchHomeScreen();

        }


        setToolbar();

        //launchFeedScreen();
        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mSearch=(ImageView)findViewById(R.id.toolbar_search_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);
        mHeld_toolbar=(Toolbar)findViewById(R.id.toolbar);
      //  setSupportActionBar(mHeld_toolbar);

        mChat.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mSearch_edt.setVisibility(View.GONE);

        mSearch_edt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearch_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callUserSearchApi();
                    } else {
                        UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "You are not connected to internet.");
                    }
                    return true;
                }
                return false;
            }
        });

        mSearch_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callUserSearchApi();
                } else {
                    UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "You are not connected to internet.");
                }
            }
        });
        mSearch_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearch.setVisibility(View.GONE);
                    mTitle.setVisibility(View.GONE);
                } else {
                    mSearch.setVisibility(View.VISIBLE);
                    mTitle.setVisibility(View.VISIBLE);
                    mSearch_edt.setVisibility(View.GONE);
                }
            }
        });



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
        intent.putExtra("fromFeed", true);
        startActivity(intent);
    }

    //private void launchChatScreen(String id, boolean isOneToOne) {
    private void launchChatScreen(String postid,boolean isOneToOne) {
        Intent intent = new Intent(FeedActivity.this, ChatActivity.class);
        intent.putExtra("postid", postid);
        intent.putExtra("isOneToOne",isOneToOne);
        //intent.putExtra("flag",flag);
        startActivity(intent);

       // mDisplayedFragment = Utils.getCurrVisibleFragment(this);
       /*
       this is commented bcoz it opens inbox we need personalchat design
       Intent intent = new Intent(FeedActivity.this, InboxActivity.class);
        //intent.putExtra("id", id);
        //intent.putExtra("isOneToOne", isOneToOne);
        startActivity(intent);*/
    }

    private void launchChatListScreen() {
        Intent intent = new Intent(FeedActivity.this, InboxActivity.class);
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
    public void launchSeenBy(String post_id){
        Intent intent = new Intent(FeedActivity.this, SeenByActivity.class);
        intent.putExtra("post_id", post_id);
        startActivity(intent);
    }
    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        Log.d(TAG, "performing action " + id);
        switch (id) {
            case AppConstants.LAUNCH_POST_SCREEN:
                launchCreatePostScreen();
                break;
            case AppConstants.LAUNCH_FEED_SCREEN:
                launchHomeScreen();
                break;
            case AppConstants.LAUNCH_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("postid"),bundle.getBoolean("oneToOne"));
                break;
            case AppConstants.LAUNCH_NOTIFICATION_SCREEN:
                launchNotificationScreen();
                break;
            case AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                launchChatListScreen();
                break;
            case AppConstants.LAUNCH_PROFILE_SCREEN:

                if (bundle != null)
                    launchProfileScreen(bundle.getString("name"));
                break;

        }
    }

    private void launchProfileScreen(String uid) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ProfileFragment.newInstance(uid), ProfileFragment.TAG, true);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
/*

        if (mDisplayedFragment instanceof FeedFragment && mDisplayedFragment.isVisible()) {

          //  this.getSupportFragmentManager().beginTransaction().remove(new FeedFragment()).commit();
            */
/*super.onBackPressed();
            updateToolbar(true, false, true, false, true, true, false, "");
            mDisplayedFragment = Utils.getCurrVisibleFragment(this);*//*

            Timber.d("finishing feed activity");

            this.finishActivity(Activity.RESULT_OK);
        }
        else {
            Timber.d("Calling super.onbackpressed");
            super.onBackPressed();
        }
*/


    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onclick detected. mPosition is " + mPosition);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
//        if(mDisplayedFragment==null)
        switch (view.getId()) {


            case R.id.toolbar_chat_img:
                Log.d(TAG, "toolbar chat image has been clicked. mPosition is " + mPosition);
//Here launch_personal_chat is repalced by launch_chat_screen
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                } else if (mPosition == 2) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                } else if (mPosition == 3) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                }
                break;
            case R.id.toolbar_notification_img:
                Log.d(TAG, "toolbar notification image has been clicked");
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 2) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 3) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                }
                break;
            case R.id.toolbar_post_img:
                Log.d(TAG, "toolbar post image has been clicked");
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_POST_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_POST_SCREEN, null);
                } else if (mPosition == 2) {
                    perform(AppConstants.LAUNCH_POST_SCREEN, null);
                } else if (mPosition == 3) {
                   perform(AppConstants.LAUNCH_POST_SCREEN, null);
                }

                break;
            case R.id.toolbar_search_img:
                Log.d(TAG, "toolbar search image has been clicked");
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
            mChat.setImageResource(R.drawable.chat);
            mCamera.setImageResource(R.drawable.camera);
        } else if (mPosition == 1) {

          //  mRetakeBtn.setVisibility(View.GONE);
         //   mPostBtn.setVisibility(View.GONE);
            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearch_edt.setVisibility(View.VISIBLE);
         //   mUsername.setVisibility(View.INVISIBLE);
            mChat.setImageResource(R.drawable.chat);
            mCamera.setImageResource(R.drawable.camera);
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

    public void hideToolbar(){
        mHeld_toolbar.setVisibility(View.GONE);
    }

    public void showToolbar(){
        mHeld_toolbar.setVisibility(View.VISIBLE);
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                mSearch_edt.getText().toString().trim(), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        DialogUtils.stopProgressDialog();

                        Bundle bundle = new Bundle();

                        bundle.putString("name", searchUserResponse.getDisplayName());
                       // bundle.putString("image", searchUserResponse.getPic());
                        perform(AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN, bundle);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "Some Problem Occurred");
                    }
                });
    }




}
