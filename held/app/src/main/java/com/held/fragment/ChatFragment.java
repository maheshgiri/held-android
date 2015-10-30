package com.held.fragment;


import android.content.BroadcastReceiver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.R;
import com.held.adapters.ChatAdapter;
import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.customview.SlideInUpAnimator;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.retrofit.response.PostResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class ChatFragment extends ParentFragment {

    public static final String TAG = ChatFragment.class.getSimpleName();
    private RecyclerView mChatList;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mChatAdapter;
    private List<PostChatData> mPostChatData = new ArrayList<>();
    private Button mSubmitBtn;
    private EditText mMessageEdt;
    private ImageView mDownLoad,mChatBackImage;
    private boolean mIsOneToOne,misLastPage =false,mIsFirstLoad=true;
    private String mId, mFriendId,mPostId,mChatBackImg;
    private BroadcastReceiver broadcastReceiver;
    private PreferenceHelper mPreference;
    private int mLimit = 7;
    private long mStart = System.currentTimeMillis();
    private PostChatData objPostChat=new PostChatData();
    private List<PostChatData> tmpList=new ArrayList<PostChatData>();
    private BlurTransformation mBlurTransformation;
    private boolean mIsScrollEndReached = false;

    public static ChatFragment newInstance(String id, boolean isOneToOne) {

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", id);
        bundle.putString("postid", id);
        bundle.putBoolean("isOneToOne", isOneToOne);
       // bundle.putString("chatBackImg",backImg);
       // Timber.d("ChatFragment new instance received arguments: user id: " + id + " isonetoone: " + isOneToOne);
        chatFragment.setArguments(bundle);
        return chatFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       // mflag=getArguments().getBoolean("flag");
        //Timber.d("ChatFragment new instance received arguments: user id: " + mId + " isonetoone: " + mIsOneToOne);

        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        HeldApplication.IS_CHAT_FOREGROUND = true;
        //LocalBroadcastManager.getInstance(getCurrActivity()).registerReceiver((broadcastReceiver),new IntentFilter("CHAT"));
    }

    @Override
    public void onPause() {
        super.onPause();
        HeldApplication.IS_CHAT_FOREGROUND = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("ChatFragment onStart called");
        HeldApplication.IS_CHAT_FOREGROUND = true;
        //LocalBroadcastManager.getInstance(getCurrActivity()).registerReceiver((broadcastReceiver),new IntentFilter("CHAT"));
    }

    @Override
    public void onStop() {
        //LocalBroadcastManager.getInstance(getCurrActivity()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        Animation animation = AnimationUtils.loadAnimation(getCurrActivity(), android.R.anim.slide_in_left);
        mIsOneToOne = getArguments().getBoolean("isOneToOne");
        mChatList = (RecyclerView) view.findViewById(R.id.CHAT_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mLayoutManager.setReverseLayout(true);
        //mLayoutManager.scrollToPositionWithOffset(mPostChatData.size(), 0);
        SlideInUpAnimator slideInUpAnimator = new SlideInUpAnimator();
        slideInUpAnimator.setAddDuration(1000);
        mChatList.setItemAnimator(slideInUpAnimator);
        mSubmitBtn = (Button) view.findViewById(R.id.CHAT_submit_btn);
        mMessageEdt = (EditText) view.findViewById(R.id.CHAT_message);
        mSubmitBtn.setOnClickListener(this);
        mBlurTransformation = new BlurTransformation(getCurrActivity(), 25f);
        mChatBackImage = (ImageView) view.findViewById(R.id.background_imageView);
        mPreference = PreferenceHelper.getInstance(getCurrActivity());
        mChatAdapter = new ChatAdapter(getCurrActivity(), mPostChatData);
        mChatList.setLayoutManager(mLayoutManager);
        mChatList.setAdapter(mChatAdapter);
        setTypeFace(mMessageEdt,"book");
        setTypeFace(mSubmitBtn,"book");
        if (getCurrActivity().getNetworkStatus()) {
            if (mIsOneToOne == true) {
                callGetUserPostApi();
                callFriendsChatsApi();
            } else {
                callSearchPostApi();
                callPostChatApi();

            }
        }



        mChatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                View view = (View) recyclerView.getChildAt(recyclerView.getChildCount() - 1);


                if(lastVisibleItemPosition == (totalItemCount -1)){
                    if(!misLastPage) {
                        if(!mIsScrollEndReached){
                            Timber.d("fetching more chats");
                            mIsScrollEndReached = true;
                            if(mIsOneToOne){
                                callFriendsChatsApi();
                            }else{
                                callPostChatApi();
                            }


                        }else{
                            Timber.d("fetching already in progress");
                        }

                    }else{
                        Timber.d("end of chat messages");
                    }
                }
            }



        });

    }



    private void callfriendChatSubmit() {

        Timber.d("Calling friend chat api");
        HeldService.getService().sendfriendChat(mPreference.readPreference(getString(R.string.API_session_token)),
                //              mId, mMessageEdt.getText().toString().trim(), "", new Callback<PostMessageResponse>() {
                getArguments().getString("user_id"), mMessageEdt.getText().toString().trim(), "", new Callback<PostMessageResponse>() {
                    @Override
                    public void success(PostMessageResponse postMessageResponse, Response response) {
                        Timber.d("##$$@@post msg response" + postMessageResponse.getFromUser().getDisplayName());
//                        objPostChat.setDate(postMessageResponse.getDate());
//                        objPostChat.setRid(postMessageResponse.getRid());
//                        objPostChat.setText(postMessageResponse.getText());
//                        objPostChat.setToUser(postMessageResponse.getToUser());
//                        objPostChat.setFromUser(postMessageResponse.getFromUser());
//                        tmpList.add(objPostChat);
//                        mPostChatData.addAll(tmpList);
                        // mChatAdapter.setPostChats(mPostChatData);
                        mMessageEdt.setText("");
                        PostChatData data = new PostChatData();
                        data.setDate(postMessageResponse.getDate());
                        data.setFromUser(postMessageResponse.getFromUser());
                        data.setToUser(postMessageResponse.getToUser());
                        data.setRid(postMessageResponse.getRid());
                        data.setText(postMessageResponse.getText());
                        mPostChatData.add(0, data);
                        mChatAdapter.notifyDataSetChanged();
                        // tmpList.clear();
                        //Timber.i("Inside chat submit",""+postMessageResponse);
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

        Timber.d("Calling friends chat api");

        HeldService.getService().getFriendChat(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), mStart, mLimit, new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        Timber.d("friends chat call success");
                        misLastPage = postChatResponse.isLastPage();
                        if (!misLastPage) {
                            mStart = postChatResponse.getNext();
                        }
                        mPostChatData.addAll(postChatResponse.getObjects());
                        mChatAdapter.setPostChats(mPostChatData);
                        mChatAdapter.notifyDataSetChanged();
                        Timber.d("resetting scroll check");
                        mIsScrollEndReached = false;
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

       // Timber.d("@@@@@Post Id from Feed" + getArguments().getString("postid"));
        HeldService.getService().getPostChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("postid"), mStart, mLimit, new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        misLastPage = postChatResponse.isLastPage();
                        if (!postChatResponse.isLastPage()) {
                            mStart = postChatResponse.getNext();
                        }
                        mPostChatData.addAll(postChatResponse.getObjects());
                        mChatAdapter.setPostChats(mPostChatData);
                        mChatAdapter.notifyDataSetChanged();
                        Timber.d("resetting scroll check");
                        mIsScrollEndReached = false;
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
                if (getCurrActivity().getNetworkStatus()) {
                    if (mIsOneToOne) {
                        if (!mMessageEdt.getText().toString().isEmpty()) {
                            callfriendChatSubmit();
                            //callFriendsChatsApi();
                        } else
                            UiUtils.showSnackbarToast(getView(), "Message should not be empty");
                    } else {
                        if (!mMessageEdt.getText().toString().isEmpty()) {
                            postGroupChat();
                        } else
                            UiUtils.showSnackbarToast(getView(), "Message should not be empty");
                    }
                }

                break;

        }
    }

    private void callDownloadRequestApi() {
        HeldService.getService().requestDownLoadPost(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), new Callback<DownloadRequestData>() {
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

    private void postGroupChat() {
        HeldService.getService().postChat(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("postid"), mMessageEdt.getText().toString().trim(), "", new Callback<PostChatData>() {
                    @Override
                    public void success(PostChatData postMessageResponse, Response response) {


                        mMessageEdt.setText("");
                        PostChatData data = new PostChatData();
                        data.setDate(postMessageResponse.getDate());
                        data.setFromUser(postMessageResponse.getUser());
                        data.setRid(postMessageResponse.getRid());
                        data.setText(postMessageResponse.getText());
                        mPostChatData.add(0, data);
                        mChatAdapter.notifyDataSetChanged();
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

    public void callSearchPostApi(){
        HeldService.getService().getSearchCurrentPost(mPreference.readPreference(getString(R.string.API_session_token)), getArguments().getString("postid"),
                new Callback<PostResponse>() {
                    @Override
                    public void success(PostResponse postResponse, Response response) {
                        PicassoCache.getPicassoInstance(getCurrActivity())
                                .load(AppConstants.BASE_URL + postResponse.getImageUri())
                                .transform(mBlurTransformation)
                                        //.placeholder(R.drawable.milana_vayntrub)
                                .into(mChatBackImage);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    public void callGetUserPostApi(){
        HeldService.getService().getUserPosts(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), mStart, mLimit, new Callback<FeedResponse>() {
                    @Override
                    public void success(FeedResponse feedResponse, Response response) {
                        PicassoCache.getPicassoInstance(getCurrActivity())
                                .load(AppConstants.BASE_URL + feedResponse.getObjects().get(0).getThumbnailUri())
                                .into(mChatBackImage);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }
    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    public void setTypeFace(Button tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
}
