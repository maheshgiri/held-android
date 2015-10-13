package com.held.fragment;


import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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

import com.held.activity.R;
import com.held.adapters.ChatAdapter;
import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.customview.SlideInUpAnimator;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

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
    private boolean mIsOneToOne,misLastPage,mIsFirstLoad=true;
    private String mId, mFriendId,mPostId,mChatBackImg;
    private BroadcastReceiver broadcastReceiver;
    private PreferenceHelper mPreference;
    private int mLimit = 5;
    private long mStart = System.currentTimeMillis();
    private PostChatData objPostChat=new PostChatData();
    private List<PostChatData> tmpList=new ArrayList<PostChatData>();
    private BlurTransformation mBlurTransformation;
   // private HandlerThread handlerThread;
    private Handler handler;
   // private boolean mflag=true;
    private Runnable runnable;

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
        LocalBroadcastManager.getInstance(getCurrActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter("CHAT"));
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
        LocalBroadcastManager.getInstance(getCurrActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter("CHAT"));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getCurrActivity()).unregisterReceiver(broadcastReceiver);
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
        // mIsOneToOne = getArguments().getBoolean("isOneToOne");
        mChatBackImage = (ImageView) view.findViewById(R.id.background_imageView);
        /*mDownLoad = (ImageView) view.findViewById(R.id.CHAT_download);
        mDownLoad.setOnClickListener(this);*/
        mPreference = PreferenceHelper.getInstance(getCurrActivity());
        runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    refreshMessages();
                    mChatList.postDelayed(this, 100);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };


       /* mChatBackImg=getArguments().getString("chatBackImg");
        PicassoCache.getPicassoInstance(getCurrActivity())
                .load(AppConstants.BASE_URL+mChatBackImg)
                .placeholder(R.drawable.milana_vayntrub)
                .into(mChatBackImage);*/
        // callFriendsChatsApi();
        mChatAdapter = null;
        if (getCurrActivity().getNetworkStatus()) {
            if (mIsOneToOne == true) {
//            mDownLoad.setVisibility(View.GONE);
                //callUserSearchApi();
                callFriendsChatsApi();
            } else {
                //if (isAdded())
                callSearchPostApi();
                callPostChatApi();

            }
        }
        mChatAdapter = new ChatAdapter(getCurrActivity(), mPostChatData);
        mChatList.setLayoutManager(mLayoutManager);
        mChatList.setAdapter(mChatAdapter);
        mChatList.scrollToPosition(mChatAdapter.getItemCount());

       // handler.postDelayed((Runnable) this, 1000);
     /*   if (getCurrActivity().getNetworkStatus()||misLastPage==false) {
            callFriendsChatsApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "Sorry! You don't seem to connected to internet");
        }*/

    /*    mChatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
               // mChatAdapter.notifyDataSetChanged();
                callFriendsChatsApi();

            }
        });*/

       mChatList.postDelayed(runnable, 100);

    }
    public void refreshMessages() {
       // mId = id;
       // mIsOneToOne = isOneToOne;
        if (mIsOneToOne) {
       //     mDownLoad.setVisibility(View.GONE);
          //  callUserSearchApi();
            callFriendsChatsApi();
            Timber.i("inside onDataRecived");
        } else {
           // if (isAdded())
                callPostChatApi();
        }
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
//                        DialogUtils.stopProgressDialog();
                        Utils.hideSoftKeyboard(getCurrActivity());
                        mFriendId = searchUserResponse.getRid();

                     /*this code should not here
                     PicassoCache.getPicassoInstance(getCurrActivity())
                                .load(AppConstants.BASE_URL + searchUserResponse.getThumbnailUri())
                                .placeholder(R.drawable.milana_vayntrub)
                                .transform(mBlurTransformation)
                                .into(mChatBackImage);*/

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
        Timber.d("Calling friend chat api");
        HeldService.getService().sendfriendChat(mPreference.readPreference(getString(R.string.API_session_token)),
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
                        callFriendsChatsApi();
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
      // callFriendsChatsApi();
    }

    private void callFriendsChatsApi() {

        Timber.d("Calling friends chat api");
        HeldService.getService().getFriendChat(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), mStart, mLimit, new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        Timber.d("friends chat call success");
                        mPostChatData.clear();
                        mPostChatData.addAll(postChatResponse.getObjects());
                        misLastPage = postChatResponse.isLastPage();
                        mChatAdapter.setPostChats(mPostChatData);
                        mChatAdapter.notifyDataSetChanged();

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
                        mPostChatData.clear();
                        mPostChatData.addAll(postChatResponse.getObjects());
                        misLastPage = postChatResponse.isLastPage();
                        mChatAdapter.setPostChats(mPostChatData);
                        mChatAdapter.notifyDataSetChanged();
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
                            callFriendChatApi();
                            //callFriendsChatsApi();
                        } else
                            UiUtils.showSnackbarToast(getView(), "Message should not be empty");
                    } else {
                        if (!mMessageEdt.getText().toString().isEmpty()) {
                            callChatPostApi();
                        } else
                            UiUtils.showSnackbarToast(getView(), "Message should not be empty");
                    }
                }

                break;
            /*case R.id.CHAT_download:
                if (getCurrActivity().getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callDownloadRequestApi();
                } else
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");

                break;*/
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

    private void callChatPostApi() {
        HeldService.getService().postChat(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("postid"), mMessageEdt.getText().toString().trim(), "", new Callback<PostMessageResponse>() {
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
       // callPostChatApi();
    }
    public void setChatBackImg(){
        Timber.i("@@@@@Chat Background");
        if(mPostChatData.size()!=0){
            PicassoCache.getPicassoInstance(getCurrActivity())
                    .load(AppConstants.BASE_URL+mPostChatData.get(0).getPost().getImageUri())
                    .placeholder(R.drawable.milana_vayntrub)
                    .into(mChatBackImage);
        }
        else {
            return;
        }

    }
    public void callSearchPostApi(){
        HeldService.getService().getSearchCurrentPost(mPreference.readPreference(getString(R.string.API_session_token)), getArguments().getString("postid"),
                new Callback<PostResponse>() {
                    @Override
                    public void success(PostResponse postResponse, Response response) {
                        PicassoCache.getPicassoInstance(getCurrActivity())
                                .load(AppConstants.BASE_URL+postResponse.getImageUri())
                                .transform(mBlurTransformation)
                                .placeholder(R.drawable.milana_vayntrub)
                                .into(mChatBackImage);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

}
