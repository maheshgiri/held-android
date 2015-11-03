package com.held.adapters;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.FeedActivity;
import com.held.activity.R;
import com.held.activity.SeenByActivity;
import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.fragment.FeedFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();

    private FeedActivity mActivity;
    private BlurTransformation mBlurTransformation;
    private GestureDetector mGestureDetector, mPersonalChatDetector;
    private String mPostId, mOwnerDisplayName,mholdId,mPostImgUrl,mUserId;
    private int mPosition;
    private FeedViewHolder feedViewHolder;
    private List<FeedData> mFeedList;
    private boolean mIsLastPage;
    private FeedFragment mFeedFragment;
    private PreferenceHelper mPreference;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isFullScreenMode = false;



    public FeedAdapter(FeedActivity activity, List<FeedData> feedDataList, BlurTransformation blurTransformation
            , boolean isLastPage, FeedFragment feedFragment) {
        mActivity = activity;
        mFeedList = feedDataList;
        mBlurTransformation = blurTransformation;
        mGestureDetector = new GestureDetector(mActivity, new GestureListener());
        mPersonalChatDetector = new GestureDetector(mActivity, new PersonalChatListener());
        mIsLastPage = isLastPage;
        mFeedFragment = feedFragment;
        mPreference=PreferenceHelper.getInstance(mActivity);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        RecyclerView.ViewHolder viewHolder;
        if (i == VIEW_ITEM) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_box,
                    parent, false);
            viewHolder = new FeedViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_progress_bar, parent, false);

            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof FeedViewHolder) {
            final FeedViewHolder holder = (FeedViewHolder) viewHolder;

           /* Picasso.with(mActivity)
                    .load(AppConstants.BASE_URL + mFeedList.get(position).getProfilePic())
                    .placeholder(R.drawable.user_icon)
                    .into(holder.mUserImg);*/

            PicassoCache.getPicassoInstance(mActivity)
                            .load(AppConstants.BASE_URL + mFeedList.get(position).getCreator().getProfilePic())
                            .placeholder(R.drawable.user_icon)
                            .into(holder.mUserImg);

            Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
            Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");

            // Set typeface of numbers as Benton Sans medium
            holder.mTimeMinTxt.setTypeface(medium);
            holder.mTimeSecTxt.setTypeface(medium);
            holder.mPersonCount.setTypeface(medium);


            // Set typeface of remaining text to Benton Sans Book
            holder.mPersonCountTxt.setTypeface(book);
            holder.mPersonCountTxt2.setTypeface(book);
            holder.mTimeTxt.setTypeface(book);
            holder.mTimeTxt2.setTypeface(book);

            //username
            holder.mUserNameTxt.setTypeface(medium);
            holder.mFeedTxt.setTypeface(book);



            holder.mPersonCount.setText(mFeedList.get(position).getViews());
            holder.mFeedTxt.setText(mFeedList.get(position).getText());

            PicassoCache.getPicassoInstance(mActivity)
                    .load(AppConstants.BASE_URL + mFeedList.get(position).getThumbnailUri())
                    .into(holder.mFeedImg);
            holder.mUserNameTxt.setText(mFeedList.get(position).getCreator().getDisplayName());
            setTimeText(mFeedList.get(position).getHeld(), holder.mTimeMinTxt, holder.mTimeSecTxt);
            if(mFeedList.get(position).getLatestMessage()!=null)
                checkLatestMessage(holder.feedStatusIcon,mFeedList.get(position).getLatestMessage().getDate());
            else if(mFeedList.get(position).getLatestHold() != null)
            {
                checkLatestHold(holder.feedStatusIcon, mFeedList.get(position).getLatestHold().getHeld());
            }

//            holder.mUserImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mActivity.perform(8, null);
//                }
//            });

            holder.mUserImg.setOnTouchListener(new View.OnTouchListener() {
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
                            mPosition = position;
                            break;
                    }
                    mOwnerDisplayName = mFeedList.get(position).getCreator().getDisplayName();
                    mUserId = mFeedList.get(position).getCreator().getRid();
                    return mPersonalChatDetector.onTouchEvent(motionEvent);
                }
            });
            holder.myCountLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPostId = mFeedList.get(position).getRid();
                    mActivity.launchSeenBy(mPostId);
                }
            });

            holder.mFeedImg.setOnTouchListener(new View.OnTouchListener() {
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
                            mPosition = position;
//                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_MOVE:
//                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            mFeedFragment.showRCView();
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            Picasso.with(mActivity).load(AppConstants.BASE_URL + mFeedList.get(position).getThumbnailUri()).
                                    into(holder.mFeedImg);
                            holder.myTimeLayout.setVisibility(View.VISIBLE);
                            if(isFullScreenMode){
                                if (!mFeedList.get(mPosition).getCreator().getDisplayName().equals(mPreference.readPreference(mActivity.getString(R.string.API_user_name)))) {
                                    callReleaseApi(mFeedList.get(position).getRid(), holder.mTimeMinTxt, holder.mTimeSecTxt, String.valueOf(System.currentTimeMillis()));
                                }
                                isFullScreenMode = false;
                            }

                            mActivity.isBlured = true;
                            mActivity.showToolbar();

                            break;

                    }
                    feedViewHolder = holder;
                    //Post id sent to chat
                    mPostImgUrl=mFeedList.get(position).getImageUri();
                    mPostId=mFeedList.get(position).getRid();
                    //mPostId = mFeedList.get(position).getCreator().getRid();
                    return mGestureDetector.onTouchEvent(motionEvent);
                }

            });
        } else {
            ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
            if (mIsLastPage) {
                holder.mIndicationTxt.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            } else {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.mIndicationTxt.setVisibility(View.GONE);
                holder.progressBar.setIndeterminate(true);
            }

        }
    }

    private void callReleaseApi(String postId, final TextView textView1,final TextView textView2,String start_tm) {
        Timber.d("calling release api");
        HeldService.getService().releasePost(mPreference.readPreference("SESSION_TOKEN"),postId,mholdId,"",String.valueOf(System.currentTimeMillis()),
                "",new Callback<ReleaseResponse>() {
                    @Override
                    public void success(ReleaseResponse releaseResponse, Response response) {
                        setTimeText(releaseResponse.getHeld(), textView1,textView2);
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

    private void setTimeText(long time, TextView textView1,TextView textView2) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        textView1.setText(String.valueOf(minutes));
        textView2.setText(String.valueOf(seconds));
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);

    }

    private void callHoldApi(String postId, final String start_tm) {
        Timber.d("calling hold api");
        HeldService.getService().holdPost(mPreference.readPreference("SESSION_TOKEN"),postId,start_tm, "",new Callback<HoldResponse>() {
            @Override
            public void success(HoldResponse holdResponse, Response response) {
                mholdId=holdResponse.getRid();
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

    @Override
    public int getItemCount() {
        return mFeedList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mFeedList.size() == position ? VIEW_PROG : VIEW_ITEM;
    }

    public void setFeedResponse(List<FeedData> feedDataList, boolean isLastPage) {
        mFeedList = feedDataList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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
        ImageView feedStatusIcon;

        private FeedViewHolder(View v) {
            super(v);

            mUserNameTxt = (TextView) v.findViewById(R.id.user_name_txt);
            mFeedImg = (ImageView) v.findViewById(R.id.post_image);
            mUserImg = (ImageView) v.findViewById(R.id.profile_img);
            mFeedTxt = (TextView) v.findViewById(R.id.post_txt);
            mTimeMinTxt = (TextView) itemView.findViewById(R.id.box_min_txt);
            mTimeSecTxt=(TextView) itemView.findViewById(R.id.box_sec_txt);
            myTimeLayout.setVisibility(View.VISIBLE);
            feedStatusIcon=(ImageView)v.findViewById(R.id.feed_status_icon);

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
            bundle.putBoolean("oneToOne",false);
           // bundle.putString("chatBackImg",mPostImgUrl); we have post img url in api
            //bundle.putBoolean("flag",false);//not necessary
            mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mActivity.getNetworkStatus()) {
//                Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedList.get(mPosition).getImage()).into(feedViewHolder.mFeedImg);
                if (!mFeedList.get(mPosition).getCreator().getDisplayName().equals(mPreference.readPreference(mActivity.getString(R.string.API_user_name)))) {
                    callHoldApi(mFeedList.get(mPosition).getRid(),String.valueOf(System.currentTimeMillis()));
                }
                feedViewHolder.myTimeLayout.setVisibility(View.INVISIBLE);
                feedViewHolder.mFeedImg.getParent().requestDisallowInterceptTouchEvent(true);
                mFeedFragment.showFullImg(AppConstants.BASE_URL + mFeedList.get(mPosition).getImageUri());
                if(e.getAction() == MotionEvent.ACTION_UP){

                }
                isFullScreenMode = true;


            } else {
                UiUtils.showSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
            }


            super.onLongPress(e);
        }

    }

    private class PersonalChatListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!mOwnerDisplayName.equals(mPreference.readPreference(mActivity.getString(R.string.API_user_name)))) {
                Bundle bundle = new Bundle();
                bundle.putString("owner_displayname", mOwnerDisplayName);
                mActivity.perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, bundle);
                return true;
            } else {
                UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You cannot chat with yourself");
                return true;
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Bundle bundle = new Bundle();
            Timber.i("User Id"+mUserId);
            bundle.putString("user_id",mUserId);
           // bundle.putString("userImg", AppConstants.BASE_URL + mFeedList.get(mPosition).getThumbnailUri());
            mActivity.perform(AppConstants.LAUNCH_PROFILE_SCREEN, bundle);

            return true;
        }

    }
    public int calculateTimeDiff(String tm){
        String s1,s2;
        long d1,d2,diff,diffHours;


        d1= Long.parseLong(tm);
        s2=String.valueOf(System.currentTimeMillis());
        d2= Long.parseLong(s2);

        diff=d2-d1;
        diffHours = diff / (60 * 60 * 1000);
        System.out.println("@@@@@  Diff hours "+diffHours);
        return (int) diffHours;
    }
    public void checkLatestMessage(ImageView img,String tm){
        int diff=calculateTimeDiff(tm);
        if(diff<=1){
            img.setImageResource(R.drawable.greenicon);
        }
    }
    public void checkLatestHold(ImageView img,String tm){
        //int diff=calculateTimeDiff(tm);
       // if(diff<=1){
            img.setImageResource(R.drawable.yellowicon);
       // }
    }

}
