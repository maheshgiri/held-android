package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.adapters.ChatAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChatFragment extends ParentFragment {

    public static final String TAG = ChatFragment.class.getSimpleName();
    private RecyclerView mChatList;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mChatAdapter;
    private List<PostChatData> mPostChatData = new ArrayList<>();
    private Button mSubmitBtn;
    private EditText mMessageEdt;
    private ImageView mBackImg;

    public static ChatFragment newInstance(String postid) {

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("postid", postid);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mChatList = (RecyclerView) view.findViewById(R.id.CHAT_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mChatAdapter = new ChatAdapter((PostActivity) getCurrActivity(), mPostChatData);
        mChatList.setLayoutManager(mLayoutManager);
        mChatList.setAdapter(mChatAdapter);
        mSubmitBtn = (Button) view.findViewById(R.id.CHAT_submit_btn);
        mMessageEdt = (EditText) view.findViewById(R.id.CHAT_message);
        mSubmitBtn.setOnClickListener(this);
        mBackImg = (ImageView) (getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_chat_img));
        mBackImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_back));
        callPostChatApi();
    }

    private void callPostChatApi() {
        HeldService.getService().getPostChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("postid"), new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        mChatAdapter.setPostChats(postChatResponse.getObjects());
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    @Override
    protected void bindListeners(View view) {
        mBackImg.setOnClickListener(this);
    }

    @Override
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.CHAT_submit_btn:
                callChatPostApi();
                break;
            case R.id.TOOLBAR_chat_img:
                mBackImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_chat));
                getCurrActivity().onBackPressed();
                break;
        }
    }

    private void callChatPostApi() {
        HeldService.getService().postChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)), getArguments().getString("postid"), mMessageEdt.getText().toString().trim(), new Callback<PostMessageResponse>() {
            @Override
            public void success(PostMessageResponse postMessageResponse, Response response) {
                mMessageEdt.setText("");
                callPostChatApi();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
