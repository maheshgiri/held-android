package com.held.adapters;


import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.ProfileActivity;
import com.held.activity.R;
import com.held.customview.BlurTransformation;
import com.held.customview.CircularImageView;
import com.held.customview.PicassoCache;
import com.held.fragment.ProfileFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.InviteResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.retrofit.response.User;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

import static com.held.utils.Utils.getString;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ProfileActivity mActivity;
    private List<FeedData> mPostList=new ArrayList<>();
    private boolean mIsLastPage;
    private String mPostId, mOwnerDisplayName;
    private int mPosition;

    private ProfileFragment mProfileFragment;
    private ItemViewHolder mItemViewHolder;
    private PreferenceHelper mPreference;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_DATA = 1;
    private static final int TYPE_FOOTER = 2;
    private BlurTransformation mBlurTransformation;
    private PreferenceHelper mPrefernce;
    private String mUserName,mprofileUrl,mUserId,mholdId;
    private User user=new User();
    private boolean isFullScreenMode = false;
    private int maxInvite=5,mInviteNo;
    private GestureDetector mGestureDetector,mGestureDetector2;
    private File mFile;
    private Uri mFileUri;
    String currentProfileUserName;


    public ProfileAdapter(ParentActivity activity,String userId,List<FeedData> postList, boolean isLastPage, ProfileFragment profileFragment) {
        mActivity =(ProfileActivity)activity;
        mPostList = postList;
        mIsLastPage = isLastPage;
        mProfileFragment = profileFragment;
        mBlurTransformation = new BlurTransformation(mActivity, 18);
        mGestureDetector = new GestureDetector(mActivity, new GestureListener());
        mGestureDetector2=new GestureDetector(mActivity,new GestureListenerProfile());
        mPreference=PreferenceHelper.getInstance(mActivity);
        Timber.i("User Name In Profile "+userId);
        mUserId=userId;
        setUserProfile();


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_profile_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_progress_bar, parent, false);
            return new ProgressViewHolder(v);
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_box, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == mPostList.size() + 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_DATA;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {

            final HeaderViewHolder viewHolderHead = (HeaderViewHolder) holder;
            Timber.i("Profile Post call Successfull ..Position:" + position);
            viewHolderHead.mUserName.setText(user.getDisplayName());
            viewHolderHead.mFriendCount.setText(user.getFriendCount());
            viewHolderHead.mPostCount.setText(user.getPostCount());
            checkAvailableInvite(user.getAvailableInvites(), viewHolderHead.mInviteCount,viewHolderHead.mInviteText);

//            PicassoCache.getPicassoInstance(mActivity)
//                    .load(AppConstants.BASE_URL + user.getProfilePic())
//                    .into(viewHolderHead.mProfilePic);
            PicassoCache.getPicassoInstance(mActivity)
                    .load(AppConstants.BASE_URL + user.getProfilePic())
                    .noFade()
                    .into(viewHolderHead.mCircularImage);

            setTypeFace(viewHolderHead.mUserName, "medium");
            setTypeFace(viewHolderHead.mFriendCount,"medium");
            setTypeFace(viewHolderHead.mPostCount,"medium");
            setTypeFace(viewHolderHead.mfriendTxt,"book");
            setTypeFace(viewHolderHead.mPostTxt,"book");
            setTypeFace(viewHolderHead.mInviteCount,"medium");
            setTypeFace(viewHolderHead.mInviteText,"book");
//            }catch (Exception e){
//                e.printStackTrace();
//            }

            viewHolderHead.mFriendReqestImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mPreference.readPreference(getString(R.string.API_user_name)).equals(user.getDisplayName())){
                        callSendFriendRequestApi(user.getRid());

                    }
                }
            });

            viewHolderHead.mCircularImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector2.onTouchEvent(event);
                }
            });


        } else if(holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            mItemViewHolder = viewHolder;
            Timber.i("Profile Post call Successfull ..Position:"+position);
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mPostList.get(position - 1).getCreator().getProfilePic()).into(viewHolder.mUserImg);
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mPostList.get(position - 1).getThumbnailUri()).into(viewHolder.mFeedImg);
            setTimeText(mPostList.get(position - 1).getHeld(), viewHolder.mTimeMinTxt, viewHolder.mTimeSecTxt);
            viewHolder.mFeedTxt.setText(mPostList.get(position - 1).getText());
            viewHolder.mUserNameTxt.setText(mPostList.get(position - 1).getCreator().getDisplayName());
            setTypeFace(viewHolder.mTimeMinTxt, "medium");
            setTypeFace(viewHolder.mTimeSecTxt,"medium");
            setTypeFace(viewHolder.mPersonCount,"medium");
            setTypeFace(viewHolder.mPersonCountTxt,"book");
            setTypeFace(viewHolder.mPersonCountTxt2,"book");
            setTypeFace(viewHolder.mTimeTxt,"book");
            setTypeFace(viewHolder.mTimeTxt2,"book");
            setTypeFace(viewHolder.mUserNameTxt,"medium");
            setTypeFace(viewHolder.mFeedTxt,"book");


           viewHolder.mFeedImg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                      /*  if (mActivity.getNetworkStatus()) {
                            Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedResponse.getObjects().get(position).getImage()).into(holder.mFeedImg);
                            callHoldApi(mFeedResponse.getObjects().get(position).getRid());
                            holder.mTimeTxt.setVisibility(View.INVISIBLE);
                        } else {
                            UiUtils.owSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
                        }*/

                            mPosition = position-1;
                            break;
                        case MotionEvent.ACTION_MOVE:
//                            mActivity.isBlured = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            mProfileFragment.showRCView();
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            Picasso.with(mActivity).load(AppConstants.BASE_URL + mPostList.get(position-1).getThumbnailUri())
                                    .into(viewHolder.mFeedImg);
                            viewHolder.myTimeLayout.setVisibility(View.VISIBLE);
                            if(isFullScreenMode){
                                if (!mPostList.get(mPosition).getCreator().getDisplayName().equals(mPreference.readPreference(mActivity.getString(R.string.API_user_name)))) {
                                    callReleaseApi(mPostList.get(position - 1).getRid(), viewHolder.mTimeMinTxt, viewHolder.mTimeSecTxt, String.valueOf(System.currentTimeMillis()));
                                }
                                isFullScreenMode = false;
                            }

                           // mActivity.isBlured = true;
                           // mActivity.showToolbar();
                            break;

                    }
                    mPostId = mPostList.get(position-1).getCreator().getRid();
                    return mGestureDetector.onTouchEvent(motionEvent);
                }

            });
        } else if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder viewHolderProgress = (ProgressViewHolder) holder;

            if (mIsLastPage) {
                viewHolderProgress.mIndicationTxt.setVisibility(View.VISIBLE);
                viewHolderProgress.progressBar.setVisibility(View.GONE);
            } else {
                viewHolderProgress.progressBar.setVisibility(View.VISIBLE);
                viewHolderProgress.mIndicationTxt.setVisibility(View.GONE);
                viewHolderProgress.progressBar.setIndeterminate(true);
            }
        }
    }

    private void callReleaseApi(String postId, final TextView textView1,final TextView textView2,String start_tm) {
        HeldService.getService().releasePost(mPreference.readPreference("SESSION_TOKEN"), postId, mholdId, "", String.valueOf(System.currentTimeMillis()),
                "", new Callback<ReleaseResponse>() {
                    @Override
                    public void success(ReleaseResponse releaseResponse, Response response) {
                        setTimeText(releaseResponse.getHeld(), textView1, textView2);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
                    }
                });
    }

    public void setPostList(List<FeedData> postList, boolean isLastPage) {
        mPostList = postList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    private void setTimeText(long time,  TextView textView1,TextView textView2) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        textView1.setText(String.valueOf(minutes));
        textView2.setText(String.valueOf(seconds));
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mPostList.size() +2;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private de.hdodenhof.circleimageview.CircleImageView mCircularImage;
        private CircularImageView mProfilePic;
        private TextView mUserName, mFriendCount, mPostCount,mfriendTxt,mPostTxt,mInviteCount,mInviteText;
        ImageView mFriendReqestImg,mChatImg;

        public HeaderViewHolder(View itemView) {
            super(itemView);
          //  mProfilePic = (com.held.customview.CircularImageView) itemView.findViewById(R.id.PROFILE_pic);
            mUserName = (TextView) itemView.findViewById(R.id.PROFILE_name);
            mFriendCount = (TextView) itemView.findViewById(R.id.PROFILE_count_friends);
            mPostCount = (TextView) itemView.findViewById(R.id.PROFILE_count_photos);
            mfriendTxt = (TextView) itemView.findViewById(R.id.PROFILE_txt_friends);
            mPostTxt = (TextView) itemView.findViewById(R.id.PROFILE_txt_photos);
            mInviteCount=(TextView) itemView.findViewById(R.id.invite_count);
            mInviteText=(TextView) itemView.findViewById(R.id.invite_txt);
            mCircularImage=(de.hdodenhof.circleimageview.CircleImageView)itemView.findViewById(R.id.circular_Profile_pic);
            mFriendReqestImg=(ImageView)itemView.findViewById(R.id.PROFILE_friendReq);
            mChatImg=(ImageView)itemView.findViewById(R.id.PROFILE_Chat);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView mUserNameTxt, mFeedTxt, mTimeMinTxt, mTimeSecTxt;
        public final ImageView mFeedImg, mUserImg;
        public final RelativeLayout myLayout = (RelativeLayout) itemView.findViewById(R.id.BOX_layout);
        public final RelativeLayout myTimeLayout = (RelativeLayout) itemView.findViewById(R.id.time_layout);
        public final LinearLayout myCountLayout = (LinearLayout) itemView.findViewById(R.id.layout_people_count);
        public final TextView mPersonCountTxt = (TextView) itemView.findViewById(R.id.tv_count_people);
        public final TextView mPersonCountTxt2 = (TextView) itemView.findViewById(R.id.tv_count_people2);
        public final TextView mPersonCount = (TextView) itemView.findViewById(R.id.count_hold_people);
        public final TextView mTimeTxt = (TextView) itemView.findViewById(R.id.time_txt);
        public final TextView mTimeTxt2 = (TextView) itemView.findViewById(R.id.time_txt2);

        public ItemViewHolder(View itemView) {
            super(itemView);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.user_name_txt);
            mFeedImg = (ImageView) itemView.findViewById(R.id.post_image);
            mUserImg = (ImageView) itemView.findViewById(R.id.profile_img);
            mFeedTxt = (TextView) itemView.findViewById(R.id.post_txt);
            mTimeMinTxt = (TextView) itemView.findViewById(R.id.box_min_txt);
            mTimeSecTxt=(TextView) itemView.findViewById(R.id.box_sec_txt);
            myTimeLayout.setVisibility(View.VISIBLE);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView mIndicationTxt;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mIndicationTxt = (TextView) v.findViewById(R.id.indication_txt);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Bundle bundle = new Bundle();
            bundle.putString("postid", mPostId);
            bundle.putBoolean("oneToOne", false);
            mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mActivity.getNetworkStatus()) {
//                Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedList.get(mPosition).getImage()).into(feedViewHolder.mFeedImg);
                if (!mPostList.get(mPosition).getCreator().getDisplayName().equals(mPreference.readPreference(mActivity.getString(R.string.API_user_name)))) {
                    callHoldApi(mPostList.get(mPosition).getRid(),String.valueOf(System.currentTimeMillis()));
                }
                mItemViewHolder.myTimeLayout.setVisibility(View.INVISIBLE);
                mItemViewHolder.mFeedImg.getParent().requestDisallowInterceptTouchEvent(true);
                mProfileFragment.showFullImg(AppConstants.BASE_URL + mPostList.get(mPosition).getImageUri());
                if(e.getAction() == MotionEvent.ACTION_UP){

                }
                isFullScreenMode = true;

            } else {
                UiUtils.showSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
            }

            super.onLongPress(e);
        }
    }
    private class GestureListenerProfile extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (currentProfileUserName.equals(mPreference.readPreference(mActivity.getString(R.string.API_user_name)))) {
                mActivity.openImageIntent();
                return true;
            } else {
                return true;
            }
        }
    }

    private void callHoldApi(String postId,String start_tm) {
        HeldService.getService().holdPost(PreferenceHelper.getInstance(mActivity).readPreference("SESSION_TOKEN"), postId, start_tm, "", new Callback<HoldResponse>() {
            @Override
            public void success(HoldResponse holdResponse, Response response) {
                mholdId = holdResponse.getRid();
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();

                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
            }
        });
    }
    public void setUserProfile()
    {

        HeldService.getService().searchUser(PreferenceHelper.getInstance(mActivity).readPreference(Utils.getString(R.string.API_session_token)),
                mUserId, new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        //Log.i("PostFragment", "@@Image Url" + searchUserResponse.getProfilePic());
                        user = searchUserResponse.getUser();
                        currentProfileUserName = searchUserResponse.getUser().getDisplayName();
                        notifyDataSetChanged();
                        Timber.i("User Init:" + user.getDisplayName());

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }
    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    void checkAvailableInvite(int count,TextView text,TextView text2){
        if(count==0)
        {   text.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            return;
        }
        if(count>maxInvite){
            text.setText("Ask For More Invites");
            text.setVisibility(View.VISIBLE);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        callAskForMoreInvites();
                }
            });
            text2.setVisibility(View.GONE);

        }else {
            text.setText(String.valueOf(count));
            text.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
        }

    }
    void callAskForMoreInvites(){

        HeldService.getService().askForMoreInvites(PreferenceHelper.getInstance(mActivity).readPreference(Utils.getString(R.string.API_session_token)), "",
                new Callback<InviteResponse>() {
                    @Override
                    public void success(InviteResponse inviteResponse, Response response) {
                        Timber.i("You Have Asked for More Invites .......");
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    public void callSendFriendRequestApi(final String friendUserId){
        HeldService.getService().sendRequests(PreferenceHelper.getInstance(mActivity).readPreference(getString(R.string.API_session_token))
                , friendUserId, "", new Callback<FriendRequestResponse>() {
            @Override
            public void success(FriendRequestResponse friendRequestResponse, Response response) {
                Timber.i("Friend Request Sent to :" + friendUserId);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }


}
