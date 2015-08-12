package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.R;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.AddFriendResponse;
import com.held.utils.PreferenceHelper;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SendFriendRequestFragment extends ParentFragment {

    public static final String TAG = SendFriendRequestFragment.class.getSimpleName();
    private TextView mUserName;
    private ImageView mProfilePic;
    private Button mSendBtn;

    public static SendFriendRequestFragment newInstance(String name, String image) {
        SendFriendRequestFragment sendFriendRequestFragment = new SendFriendRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("image", image);
        sendFriendRequestFragment.setArguments(bundle);
        return sendFriendRequestFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_friend_request, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mUserName = (TextView) view.findViewById(R.id.BOX_user_name_txt);
        mProfilePic = (ImageView) view.findViewById(R.id.BOX_main_img);
        mSendBtn = (Button) view.findViewById(R.id.FR_send_btn);
        mSendBtn.setOnClickListener(this);

        Picasso.with(getCurrActivity()).load(getArguments().getString("image")).into(mProfilePic);
        mUserName.setText(getArguments().getString("name"));
    }

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.FR_send_btn:
                callSendRequestAPi();
                break;
        }
    }

    private void callSendRequestAPi() {
        HeldService.getService().addFriend(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)), getArguments().getString("name"), new Callback<AddFriendResponse>() {
            @Override
            public void success(AddFriendResponse addFriendResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
