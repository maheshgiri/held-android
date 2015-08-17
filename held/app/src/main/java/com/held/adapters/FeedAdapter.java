package com.held.adapters;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.customview.BlurTransformation;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();

    private PostActivity mActivity;
    private BlurTransformation mBlurTransformation;
    private GestureDetector mGestureDetector, mPersonalChatDetector;
    private String mPostId, mOwnerDisplayName;
    private int mPosition;
    private FeedViewHolder feedViewHolder;
    private List<FeedData> mFeedList;
    private boolean mIsLastPage;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public FeedAdapter(PostActivity activity, List<FeedData> feedDataList, BlurTransformation blurTransformation
            , boolean isLastPage) {
        mActivity = activity;
        mFeedList = feedDataList;
        mBlurTransformation = blurTransformation;
        mGestureDetector = new GestureDetector(mActivity, new GestureListener());
        mPersonalChatDetector = new GestureDetector(mActivity, new PersonalChatListener());
        mIsLastPage = isLastPage;
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
            holder.mUserNameTxt.setText(mFeedList.get(position).getOwner_display_name());
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mFeedList.get(position).getOwner_pic()).into(holder.mUserImg);
            holder.mFeedTxt.setText(mFeedList.get(position).getText());
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mFeedList.get(position).getImage()).
                    transform(mBlurTransformation).into(holder.mFeedImg);

            setTimeText(mFeedList.get(position).getHeld(), holder.mTimeTxt);

            holder.mUserImg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mOwnerDisplayName = mFeedList.get(position).getOwner_display_name();
                    return mPersonalChatDetector.onTouchEvent(motionEvent);
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
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mActivity.isBlured = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedList.get(position).getImage()).
                                    transform(mBlurTransformation).into(holder.mFeedImg);
                            holder.mTimeTxt.setVisibility(View.VISIBLE);
                            callReleaseApi(mFeedList.get(position).getRid(), holder.mTimeTxt);
                            mActivity.isBlured = true;
                            break;

                    }
                    feedViewHolder = holder;
                    mPostId = mFeedList.get(position).getRid();
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

    private void callReleaseApi(String postId, final TextView textView) {
        HeldService.getService().releasePost(postId, System.currentTimeMillis(), PreferenceHelper.getInstance(mActivity).readPreference("SESSION_TOKEN"),
                new Callback<ReleaseResponse>() {
                    @Override
                    public void success(ReleaseResponse releaseResponse, Response response) {
                        setTimeText(releaseResponse.getHeld(), textView);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void setTimeText(long time, TextView textView) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        textView.setText(minutes + " Minutes " + seconds + " Seconds");
        textView.setVisibility(View.VISIBLE);
    }

    private void callHoldApi(String postId) {
        HeldService.getService().holdPost(postId, PreferenceHelper.getInstance(mActivity).readPreference("SESSION_TOKEN"), new Callback<HoldResponse>() {
            @Override
            public void success(HoldResponse holdResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

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
        private final TextView mUserNameTxt, mFeedTxt, mTimeTxt;
        private final ImageView mFeedImg, mUserImg;

        private FeedViewHolder(View v) {
            super(v);
            mUserNameTxt = (TextView) v.findViewById(R.id.BOX_user_name_txt);
            mFeedImg = (ImageView) v.findViewById(R.id.BOX_main_img);
            mUserImg = (ImageView) v.findViewById(R.id.BOX_profile_img);
            mFeedTxt = (TextView) v.findViewById(R.id.BOX_des_txt);
            mTimeTxt = (TextView) v.findViewById(R.id.BOX_time_txt);
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
            mActivity.perform(2, bundle);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mActivity.getNetworkStatus()) {
                Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedList.get(mPosition).getImage()).into(feedViewHolder.mFeedImg);
                callHoldApi(mFeedList.get(mPosition).getRid());
                feedViewHolder.mTimeTxt.setVisibility(View.INVISIBLE);
                feedViewHolder.mFeedImg.getParent().requestDisallowInterceptTouchEvent(true);
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
            Bundle bundle = new Bundle();
            bundle.putString("owner_displayname", mOwnerDisplayName);
            mActivity.perform(6, bundle);
            return true;
        }
    }
}
