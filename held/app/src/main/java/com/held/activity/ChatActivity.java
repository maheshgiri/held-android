package com.held.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.ParentFragment;
import com.held.utils.AndroidBug5497Workaround;

/**
 * Created by swapnil on 3/10/15.
 */
public class ChatActivity extends ParentActivity implements View.OnClickListener{

    ImageView mChat, mCamera, mNotification,mSearch;
    TextView mInvite;
    EditText mSearchEdt;
    Activity mActivity;
    Fragment mDisplayFragment;
    String mId,mChatBackImg;
    boolean flag;
    View statusBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

            statusBar.setVisibility(View.VISIBLE);
            AndroidBug5497Workaround.assistActivity(this);
        }else {
            statusBar.setVisibility(View.GONE);
        }

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity = this;
        TextView title = (TextView)findViewById(R.id.toolbar_title_txt);
        title.setText("");
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setVisibility(View.GONE);
        mSearchEdt.setVisibility(View.GONE);
        mChat.setImageResource(R.drawable.back);
        mCamera.setImageResource(R.drawable.menu);
        mCamera.setVisibility(View.VISIBLE);
        mSearch=(ImageView) findViewById(R.id.toolbar_search_img);
        mSearch.setVisibility(View.GONE);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mChat.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        boolean isOneToOne = extras.getBoolean("isOneToOne");
        mId=extras.getString("id");
       // mChatBackImg=extras.getString("chatBackImg");
       // flag=extras.getBoolean("flag");
       // String chatId = extras.getString("chatId");
       // Timber.d("Chat activity received chat id " + chatId + " isontotone: " + isOneToOne);
        launchChatScreen(mId, isOneToOne);
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        ParentFragment frag = ChatFragment.newInstance(id, isOneToOne);
        Bundle bundle=new Bundle();

        if(isOneToOne)
            bundle.putString("user_id",id);
        else
            bundle.putString("postid",id);
        //bundle.putString("chatBackImg",mChatBackImg);
        bundle.putBoolean("isOneToOne",isOneToOne);
        frag.setArguments(bundle);
        addFragment(ChatFragment.newInstance(id,isOneToOne), ChatFragment.TAG, false);
        mDisplayFragment = frag;
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;


            case R.id.toolbar_post_img:
                // todo:
                break;

            case R.id.toolbar_chat_img:
                onBackPressed();
                break;
        }
    }

    private void launchCreatePostScreen() {
        Intent intent = new Intent(ChatActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(ChatActivity.this, NotificationActivity.class);
        startActivity(intent);
    }





}
