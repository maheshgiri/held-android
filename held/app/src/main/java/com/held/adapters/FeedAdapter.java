package com.held.adapters;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.customview.BlurTransformation;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();

    private PostActivity mActivity;
    private FeedResponse mFeedResponse;
    private BlurTransformation mBlurTransformation;
    private GestureDetector mGestureDetector;
    private String mPostId;

    public FeedAdapter(PostActivity activity, FeedResponse feedResponse, BlurTransformation blurTransformation) {
        mActivity = activity;
        mFeedResponse = feedResponse;
        mBlurTransformation = blurTransformation;
        mGestureDetector = new GestureDetector(mActivity, new GestureListener());
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_box,
                parent, false);
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, final int position) {
        holder.mUserNameTxt.setText(mFeedResponse.getObjects().get(position).getOwner_display_name());
        Picasso.with(mActivity).load(AppConstants.BASE_URL + mFeedResponse.getObjects().get(position).getOwner_pic()).into(holder.mUserImg);
        holder.mFeedTxt.setText(mFeedResponse.getObjects().get(position).getText());
        Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedResponse.getObjects().get(position).getImage()).
                transform(mBlurTransformation).into(holder.mFeedImg);

        setTimeText(mFeedResponse.getObjects().get(position).getHeld(), holder.mTimeTxt);

        holder.mFeedImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mActivity.getNetworkStatus()) {
                            Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedResponse.getObjects().get(position).getImage()).into(holder.mFeedImg);
                            callHoldApi(mFeedResponse.getObjects().get(position).getRid());
                            holder.mTimeTxt.setVisibility(View.INVISIBLE);
                        } else {
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedResponse.getObjects().get(position).getImage()).
                                transform(mBlurTransformation).into(holder.mFeedImg);
                        holder.mTimeTxt.setVisibility(View.VISIBLE);
                        callReleaseApi(mFeedResponse.getObjects().get(position).getRid(), holder.mTimeTxt);
                        break;

                }
                mPostId = mFeedResponse.getObjects().get(position).getRid();
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
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
        if (mFeedResponse.getObjects() != null)
            return mFeedResponse.getObjects().size();
        else return 0;
    }

    public void setFeedResponse(FeedResponse feedResponse) {
        mFeedResponse = feedResponse;
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
    }

}
