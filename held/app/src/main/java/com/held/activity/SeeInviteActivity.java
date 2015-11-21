package com.held.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.held.adapters.SeeInviteAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.InviteListResponse;
import com.held.retrofit.response.InviteResponse;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SeeInviteActivity extends ParentActivity{

    ListView inviteListView;
    SeeInviteAdapter seeInviteAdapter;
    ArrayList<InviteResponse>inviteList=new ArrayList<>();
    PreferenceHelper mPreference;
    private int mLimit = 5;
    private long mStart = System.currentTimeMillis();
    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_invite);

        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mSearch=(ImageView)findViewById(R.id.toolbar_search_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mTitle.setText("See Invites");
        mInvite.setVisibility(View.GONE);
        mSearch_edt.setVisibility(View.GONE);
        mSearch.setVisibility(View.GONE);
        mNotification.setVisibility(View.GONE);
        mCamera.setVisibility(View.GONE);
        mChat.setImageResource(R.drawable.back);
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mPreference=PreferenceHelper.getInstance(this);
        inviteListView=(ListView)findViewById(R.id.seeInviteListView);
        callGetInviteListByUser();
        seeInviteAdapter=new SeeInviteAdapter(inviteList,this);
        inviteListView.setAdapter(seeInviteAdapter);

    }
    void callGetInviteListByUser(){
        HeldService.getService().getInvitationList(mPreference.readPreference(getString(R.string.API_session_token))
                , mStart, mLimit, new Callback<InviteListResponse>() {
            @Override
            public void success(InviteListResponse inviteListResponse, Response response) {
                inviteList=inviteListResponse.getObjects();
                Timber.i("Data Recived :"+inviteList.get(0).getPhone());
//                seeInviteAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
