package com.held.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.fragment.FeedFragment;
import com.held.fragment.HomeFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.InviteResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.CustomContact;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class FeedActivity extends ParentActivity implements View.OnClickListener {

    private static final int PICK_CONTACT =1 ;
    int  max_pic_contact = 5;
    //    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite;
    private GestureDetector gestureDetector;
    protected Toolbar mHeld_toolbar;
    private final String TAG = "FeedActivity";
    private RelativeLayout mPosttoolbar,statusbar;
    private int mPosition = 1;
    private PreferenceHelper mPreference;
    View statusBar;
    private boolean firstClick=true;
    private String mUserNameForSearch;
    CustomContact personContact=new CustomContact();
    ArrayList<CustomContact> inviteContactList=new ArrayList<>(max_pic_contact);
//    private View toolbar_divider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("starting feed activity");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


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
//        getSupportActionBar().setCustomView(R.layout.app_toolbar);

//        getSupportActionBar().hide();
//        statusbar=(RelativeLayout)findViewById(R.id.statusbar);
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
//        {
//            statusbar.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            statusbar.setVisibility(View.GONE);
//        }


//        if (getIntent() != null && getIntent().getExtras() != null) {
//            if (getIntent().getExtras().getBoolean("isProfile")) {
//                launchProfileScreen(mPreference.readPreference(getString(R.string.API_user_name)));
//            }
//        } else {

//            launchHomeScreen();

//        }


        //setToolbar();

        //launchFeedScreen();
        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mSearch=(ImageView)findViewById(R.id.toolbar_search_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mInvite.setTypeface(medium);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);

        launchFeedScreen();
//        setSupportActionBar(mHeld_toolbar);
//        getSupportActionBar().getThemedContext();
      //  setSupportActionBar(mHeld_toolbar);

        mChat.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mSearch_edt.setVisibility(View.GONE);
        mInvite.setOnClickListener(this);

        mSearch_edt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
//        toolbar_divider=(View)findViewById(R.id.toolbar_divider);
//        mSearch_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    if (getNetworkStatus()) {
//                        DialogUtils.showProgressBar();
//                        //callUserSearchApi();
//                    } else {
//                        UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "You are not connected to internet.");
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

//       try{
//           mSearch_edt.addTextChangedListener(new TextWatcher() {
//               @Override
//               public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//               }
//
//               @Override
//               public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//               }
//
//               @Override
//               public void afterTextChanged(Editable editable) {
//                   if (getNetworkStatus()) {
//                       DialogUtils.showProgressBar();
//                      // callUserSearchApi();
//                   } else {
//                       UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "You are not connected to internet.");
//                   }
//               }
//           });
//       }catch (Exception e){
//           e.printStackTrace();
//       }
//        Window window = this.getWindow();
//        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        mSearch_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //mSearch.setVisibility(View.GONE);
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
    private void launchChatScreen(String id,boolean isOneToOne) {
        Intent intent = new Intent(FeedActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        //intent.putExtra("chatBackImg",backImg);
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
                    launchChatScreen(bundle.getString("id"),bundle.getBoolean("oneToOne"));
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
                    launchProfileScreen(bundle.getString("user_id"));
                break;
        }
    }

    private void launchProfileScreen(String uid) {
        Intent intent = new Intent(FeedActivity.this, ProfileActivity.class);
        intent.putExtra("user_id", uid);
        startActivity(intent);
    }
    private void launchSearchScreen(String uname) {
        Intent intent = new Intent(FeedActivity.this, SearchActivity.class);
        intent.putExtra("userName", uname);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if (mDisplayedFragment instanceof FeedFragment && mDisplayedFragment.isVisible()) {
            this.finishActivity(Activity.RESULT_OK);
            this.finish();
          //  this.getSupportFragmentManager().beginTransaction().remove(new FeedFragment()).commit();

/*super.onBackPressed();
            updateToolbar(true, false, true, false, true, true, false, "");
            mDisplayedFragment = Utils.getCurrVisibleFragment(this);*//*

            Timber.d("finishing feed activity");

            */
        }
        else {
            Timber.d("Calling super.onbackpressed");
            super.onBackPressed();
        }



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
                 if(firstClick) {
                     visibleTextView();
                     firstClick=false;
                 }
                else {
                     mUserNameForSearch= mSearch_edt.getText().toString();
                     Timber.i("User Name for search :"+mUserNameForSearch);
                     mSearch_edt.setText("");
                     hideTextView();
                     firstClick=true;
                     launchSearchScreen(mUserNameForSearch);

                 }

                break;
            case R.id.toolbar_invite_txt:
                inviteNewUser();
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
        mSearch_edt.setFocusable(true);
        mSearch_edt.setFocusableInTouchMode(true);
        mSearch_edt.requestFocus();
        mTitle.setVisibility(View.GONE);
        mSearch.setVisibility(View.VISIBLE);

    }
    public void hideTextView(){

        mSearch_edt.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
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
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
        statusBar.setVisibility(View.GONE);}
//        toolbar_divider.setVisibility(View.GONE);
    }

    public void showToolbar(){
        mHeld_toolbar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
        statusBar.setVisibility(View.VISIBLE);}
//        toolbar_divider.setVisibility(View.VISIBLE);
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

    void inviteNewUser(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String phn_no="",name;
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id=c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone=c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            phn_no = phones.getString(phones.getColumnIndex("data1"));
                            phn_no=phn_no.replaceAll(" ","");
                        }
                            name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME));
                            Timber.i("Contact Picked :"+name+":"+phn_no);
                        personContact.setName(name);
                        personContact.setPhone_no(phn_no);
                        callInviteUser(personContact.getPhone_no());


                    }
                }
        }
    }

    void callInviteUser(String phone){
        HeldService.getService().sendInvitation(mPreference.readPreference(getString(R.string.API_session_token)), phone, "",
                new Callback<InviteResponse>() {
                    @Override
                    public void success(InviteResponse inviteResponse, Response response) {
                        Timber.i("Invittion Sent To :"+inviteResponse.getPhone()+":Successfully..");
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }



}
