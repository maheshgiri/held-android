package com.held.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ParentFragment;
import com.held.receiver.NetworkStateReceiver;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.NetworkUtil;
import com.held.utils.UiUtils;

import org.w3c.dom.Text;

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
        DialogUtils.resetDialog(ParentActivity.this);
        //Get current Network status during Activity creation for first time
        mNetworkStatus = NetworkUtil.isInternetConnected(getApplicationContext());

        //Register here to get Network status
        NetworkStateReceiver.registerOnNetworkChangeListener(this);

        mFragEnterAnim = R.anim.slide_in_right;
        mFragExitAnim = R.anim.slide_out_left;
        mFragPopEnterAnim = R.anim.slide_in_left;
        mFragPopExitAnim = R.anim.slide_out_right;

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
        mDisplayedFragment=fragment;

        addFragment(fragment, tag, false,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void replaceFragment(ParentFragment fragment, String tag) {
        mDisplayedFragment=fragment;

        replaceFragment(fragment, tag, false,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void addFragment(ParentFragment fragment, String tag, boolean addToBackStack) {
        mDisplayedFragment=fragment;
        addFragment(fragment, tag, addToBackStack,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void replaceFragment(ParentFragment fragment, String tag, boolean addToBackStack) {
        mDisplayedFragment=fragment;
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


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        }
        if (mToolbar == null) return;

        ImageView chat_img=(ImageView)findViewById(R.id.toolbar_chat_img);
        ImageView search_img=(ImageView)findViewById(R.id.toolbar_search_img);
        ImageView notification_img=(ImageView)findViewById(R.id.toolbar_notification_img);
        ImageView camera_img=(ImageView)findViewById(R.id.toolbar_post_img);
        TextView toolbar_title_txt=(TextView)findViewById(R.id.toolbar_title_txt);
        EditText toolbar_search_edt_txt=(EditText)findViewById(R.id.toolbar_search_edt_txt);


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