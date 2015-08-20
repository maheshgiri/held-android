package com.held.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.held.fragment.NotificationFragment;

public class NotificationActivity extends ParentActivity {

    private Fragment mDisplayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        launchNotificationScreen();
    }

    private void launchNotificationScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(), NotificationFragment.TAG);
        mDisplayFragment = NotificationFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

