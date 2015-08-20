package com.held.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.held.fragment.ChatFragment;
import com.held.fragment.FriendsListFragment;

public class ChatActivity extends ParentActivity {

    private Fragment mDisplayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getIntent().getExtras() != null) {
            launchChatScreen(getIntent().getExtras().getString("id"), getIntent().getExtras().getBoolean("isOneToOne"));
        } else {
            launchInboxPage();
        }
    }

    private void launchInboxPage() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FriendsListFragment.newInstance(), FriendsListFragment.TAG);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    private void launchChatScreenFromInbox(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG, true);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case 0:
                if (bundle != null)
                    launchChatScreenFromInbox(bundle.getString("owner_displayname"), true);
                break;
        }
    }
}
