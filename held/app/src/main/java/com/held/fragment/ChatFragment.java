package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.held.activity.ChatActivity;
import com.held.activity.R;
import com.held.adapters.ChatAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ChatFragment extends ParentFragment {

    public static final String TAG = ChatFragment.class.getSimpleName();
    private RecyclerView mChatList;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mChatAdapter;
    private List<PostChatData> mPostChatData = new ArrayList<>();
    private Button mSubmitBtn;
    private EditText mMessageEdt;
    private ImageView mDownLoad;
    private boolean mIsOneToOne;
    private String mId, mFriendId;

    public static ChatFragment newInstance(String id, boolean isOneToOne) {

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putBoolean("isOneToOne", isOneToOne);
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
        mChatAdapter = new ChatAdapter((ChatActivity) getCurrActivity(), mPostChatData);
        mChatList.setLayoutManager(mLayoutManager);
        mChatList.setAdapter(mChatAdapter);
        mSubmitBtn = (Button) view.findViewById(R.id.CHAT_submit_btn);
        mMessageEdt = (EditText) view.findViewById(R.id.CHAT_message);
        mSubmitBtn.setOnClickListener(this);
        mIsOneToOne = getArguments().getBoolean("isOneToOne");
        mDownLoad = (ImageView) view.findViewById(R.id.CHAT_download);
        mDownLoad.setOnClickListener(this);
        mId = getArguments().getString("id");
        if (mIsOneToOne) {
            mDownLoad.setVisibility(View.GONE);
            callUserSearchApi();
        } else {
            callPostChatApi();
        }
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("id"), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
//                        DialogUtils.stopProgressDialog();
                        Utils.hideSoftKeyboard(getCurrActivity());
                        mFriendId = searchUserResponse.getRid();
//                        if (!mMessageEdt.getText().toString().isEmpty())
                        callFriendsChatsApi();
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    private void callFriendChatApi() {
        HeldService.getService().friendChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                mFriendId, mMessageEdt.getText().toString().trim(), new Callback<PostMessageResponse>() {
                    @Override
                    public void success(PostMessageResponse postMessageResponse, Response response) {
                        mMessageEdt.setText("");
                        callFriendsChatsApi();
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    private void callFriendsChatsApi() {
        HeldService.getService().getFriendChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                mFriendId, new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        mChatAdapter.setPostChats(postChatResponse.getObjects());
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    private void callPostChatApi() {
        HeldService.getService().getPostChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("id"), new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        mChatAdapter.setPostChats(postChatResponse.getObjects());
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    @Override
    protected void bindListeners(View view) {
    }

    @Override
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.CHAT_submit_btn:
                if (mIsOneToOne) {
                    if (!mMessageEdt.getText().toString().isEmpty())
                        callFriendChatApi();
                    else
                        UiUtils.showSnackbarToast(getView(), "Message should not be empty");
                } else {
                    if (!mMessageEdt.getText().toString().isEmpty())
                        callChatPostApi();
                    else
                        UiUtils.showSnackbarToast(getView(), "Message should not be empty");
                }
                break;
            case R.id.CHAT_download:
                if (getCurrActivity().getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callDownloadRequestApi();
                } else
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");

                break;
        }
    }

    private void callDownloadRequestApi() {
        HeldService.getService().requestDownLoadPost(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("id"), new Callback<DownloadRequestData>() {
                    @Override
                    public void success(DownloadRequestData downloadRequestData, Response response) {
                        DialogUtils.stopProgressDialog();
                        UiUtils.showSnackbarToast(getView(), "Download Request Sent Successfully");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    private void callChatPostApi() {
        HeldService.getService().postChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("id"), mMessageEdt.getText().toString().trim(), new Callback<PostMessageResponse>() {
                    @Override
                    public void success(PostMessageResponse postMessageResponse, Response response) {
                        mMessageEdt.setText("");
                        callPostChatApi();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }
}
