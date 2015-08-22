package com.held.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ParentFragment;
import com.held.receiver.NetworkStateReceiver;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.NetworkUtil;

import java.util.List;

public abstract class ParentActivity extends AppCompatActivity implements NetworkStateReceiver.OnNetworkChangeListener {

    private static final String TAG = ParentActivity.class.getSimpleName();

    protected Toolbar mToolbar;

    protected boolean mNetworkStatus;
    // Fragment related: ContainerId, Currently Displayed Fragment, add/replace Animations.
    protected int mContainerID;
    protected ParentFragment mDisplayedFragment;
    protected int mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim;
    private boolean mShowChat, mShowRetakeBtn, mShowSearch, mShowUserName, mShowCamera, mShowNotification, mShowPostBtn;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Handle if Font style changed from Device setting(Specially Samsung S3, S4..), then current activity is going to restart again.
        if (savedInstanceState != null) {

        }
        DialogUtils.resetDialog(this);
        //Get current Network status during Activity creation for first time
        mNetworkStatus = NetworkUtil.isInternetConnected(getApplicationContext());

        //Register here to get Network status
        NetworkStateReceiver.registerOnNetworkChangeListener(this);

//        mFragEnterAnim = R.anim.slide_in_right;
//        mFragExitAnim = R.anim.slide_out_left;
//        mFragPopEnterAnim = R.anim.slide_in_left;
//        mFragPopExitAnim = R.anim.slide_out_right;

    }

//    protected void setStatusBarColor() {
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintColor(Utilities.getColor(R.color.toolbar_bg_blue));
//        }
//    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mContainerID = R.id.frag_container;

    }

    protected void removeAllFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    protected void addFragment(ParentFragment fragment, String tag) {
        addFragment(fragment, tag, false,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void replaceFragment(ParentFragment fragment, String tag) {
        replaceFragment(fragment, tag, false,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void addFragment(ParentFragment fragment, String tag, boolean addToBackStack) {
        addFragment(fragment, tag, addToBackStack,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void replaceFragment(ParentFragment fragment, String tag, boolean addToBackStack) {
        replaceFragment(fragment, tag, addToBackStack,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void addFragment(ParentFragment fragment, String tag, boolean addToBackStack,
                               int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        mDisplayedFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
                .add(mContainerID, fragment, tag);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    protected void replaceFragment(ParentFragment fragment, String tag, boolean addToBackStack,
                                   int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        mDisplayedFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
                .replace(mContainerID, fragment, tag);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HeldApplication.IS_APP_FOREGROUND = true;
        //Get current Network status during Activity resume
        mNetworkStatus = NetworkUtil.isInternetConnected(getApplicationContext());
    }

    @Override
    protected void onPause() {
        HeldApplication.IS_APP_FOREGROUND = false;
        super.onPause();
    }

    //Called during Network status changed
    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mNetworkStatus = isEnabled;
    }

    public boolean getNetworkStatus() {
        return mNetworkStatus;
    }

    @Override
    protected void onDestroy() {

//        MusicUtils.stopMusic();
        //Unregister here (remove listener)
        NetworkStateReceiver.unregisterOnNetworkChangeListener(this);

        super.onDestroy();
    }


//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//    }
//
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        if (mToolbar == null) return;

        ImageView chatImg = (ImageView) findViewById(R.id.TOOLBAR_chat_img);
        ImageView cameraImg = (ImageView) findViewById(R.id.TOOLBAR_camera_img);
        ImageView notificationImg = (ImageView) findViewById(R.id.TOOLBAR_notification_img);
        EditText searchEdt = (EditText) findViewById(R.id.TOOLBAR_search_edt);
        Button retakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
        Button postBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
        TextView userNameTxt = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        if (mShowChat) chatImg.setVisibility(View.VISIBLE);
        else chatImg.setVisibility(View.INVISIBLE);

        if (mShowCamera) cameraImg.setVisibility(View.VISIBLE);
        else cameraImg.setVisibility(View.INVISIBLE);

        if (mShowNotification) notificationImg.setVisibility(View.VISIBLE);
        else notificationImg.setVisibility(View.INVISIBLE);

        if (mShowSearch) searchEdt.setVisibility(View.VISIBLE);
        else searchEdt.setVisibility(View.INVISIBLE);

        if (mShowUserName) {
            userNameTxt.setVisibility(View.VISIBLE);
            userNameTxt.setText(mUserName);
        } else userNameTxt.setVisibility(View.INVISIBLE);

        if (mShowRetakeBtn) retakeBtn.setVisibility(View.VISIBLE);
        else retakeBtn.setVisibility(View.INVISIBLE);

        if (mShowPostBtn) postBtn.setVisibility(View.VISIBLE);
        else postBtn.setVisibility(View.INVISIBLE);

    }

    public void updateToolbar(boolean showChat, boolean showRetakeBtn, boolean showSearchBar, boolean showUserName,
                              boolean showCamera, boolean showNotification, boolean showPostBtn, String userNameTxt) {

        mShowChat = showChat;
        mShowRetakeBtn = showRetakeBtn;
        mShowUserName = showUserName;
        mShowNotification = showNotification;
        mShowCamera = showCamera;
        mShowSearch = showSearchBar;
        mShowPostBtn = showPostBtn;
        mUserName = userNameTxt;

        setToolbar();
    }

    public void perform(int id, Bundle bundle) {

    }
}