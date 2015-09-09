package com.held.adapters;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.customview.BlurTransformation;
import com.held.fragment.ProfileFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParentActivity mActivity;
    private List<FeedData> mPostList;
    private boolean mIsLastPage;
    private String mPostId, mOwnerDisplayName;
    private int mPosition;
    private GestureDetector mGestureDetector;
    private ProfileFragment mProfileFragment;
    private ItemViewHolder mItemViewHolder;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_DATA = 1;
    private static final int TYPE_FOOTER = 2;
    private BlurTransformation mBlurTransformation;


    public ProfileAdapter(ParentActivity activity, List<FeedData> postList, boolean isLastPage, ProfileFragment profileFragment) {
        mActivity = activity;
        mPostList = postList;
        mIsLastPage = isLastPage;
        mProfileFragment = profileFragment;
        mBlurTransformation = new BlurTransformation(mActivity, 18);
        mGestureDetector = new GestureDetector(mActivity, new GestureListener());
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
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
           Picasso.with(mActivity).load(AppConstants.BASE_URL + "/user_images/tejasshah_1441082308661.jpg").into(viewHolder.mProfilePic);
        } else if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder viewHolder = (ProgressViewHolder) holder;

            if (mIsLastPage) {
                viewHolder.mIndicationTxt.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
            } else {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.mIndicationTxt.setVisibility(View.GONE);
                viewHolder.progressBar.setIndeterminate(true);
            }
        } else {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            mItemViewHolder = viewHolder;
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mPostList.get(position - 1).getOwner_pic()).into(viewHolder.mUserImg);
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mPostList.get(position - 1).getImageUri()).transform(mBlurTransformation).into(viewHolder.mFeedImg);
            setTimeText(mPostList.get(position - 1).getHeld(), viewHolder.mTimeTxt);
            viewHolder.mFeedTxt.setText(mPostList.get(position - 1).getText());
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
                            mPosition = position - 1;
                            break;
                        case MotionEvent.ACTION_MOVE:
//                            mActivity.isBlured = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            mProfileFragment.showRCView();
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            Picasso.with(mActivity).load("http://139.162.1.137/api" + mPostList.get(position - 1).getImageUri()).
                                    transform(new BlurTransformation(mActivity, 18)).into(viewHolder.mFeedImg);
                            viewHolder.mTimeTxt.setVisibility(View.VISIBLE);
                            callReleaseApi(mPostList.get(position - 1).getRid(), viewHolder.mTimeTxt,String.valueOf(System.currentTimeMillis()));
//                            mActivity.isBlured = true;
                            break;

                    }
                    mPostId = mPostList.get(position - 1).getRid();
                    return mGestureDetector.onTouchEvent(motionEvent);
                }

            });
        }
    }

    private void callReleaseApi(String postId, final TextView textView,String start_tm) {
        HeldService.getService().releasePost(PreferenceHelper.getInstance(mActivity).readPreference("SESSION_TOKEN"),postId,start_tm ,String.valueOf(System.currentTimeMillis()),"",
                new Callback<ReleaseResponse>()  {
                    @Override
                    public void success(ReleaseResponse releaseResponse, Response response) {
                        setTimeText(releaseResponse.getHeld(), textView);
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

    private void setTimeText(long time, TextView textView) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        textView.setText(minutes + " Minutes " + seconds + " Seconds");
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mPostList.size() + 2;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePic;
        private TextView mUserName, mFriendCount, mPostCount;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.PROFILE_pic);
            mUserName = (TextView) itemView.findViewById(R.id.PROFILE_name);
            mFriendCount = (TextView) itemView.findViewById(R.id.PROFILE_friends);
            mPostCount = (TextView) itemView.findViewById(R.id.PROFILE_photos);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView mUserNameTxt, mFeedTxt, mTimeTxt;
        private final ImageView mFeedImg, mUserImg;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.BOX_user_name_txt);
            mFeedImg = (ImageView) itemView.findViewById(R.id.BOX_main_img);
            mUserImg = (ImageView) itemView.findViewById(R.id.BOX_profile_img);
            mFeedTxt = (TextView) itemView.findViewById(R.id.BOX_des_txt);
            mTimeTxt = (TextView) itemView.findViewById(R.id.BOX_time_txt);
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
//                Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedList.get(mPosition).getImage()).into(feedViewHolder.mFeedImg);
                if (!mPostList.get(mPosition).getOwner_display_name().equals(PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_user_name)))) {
                    callHoldApi(mPostList.get(mPosition).getRid(),String.valueOf(System.currentTimeMillis()));
                }
                mItemViewHolder.mTimeTxt.setVisibility(View.INVISIBLE);
                mItemViewHolder.mFeedImg.getParent().requestDisallowInterceptTouchEvent(true);
                mProfileFragment.showFullImg(AppConstants.BASE_URL + mPostList.get(mPosition).getImageUri());

            } else {
                UiUtils.showSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
            }


            super.onLongPress(e);
        }
    }

    private void callHoldApi(String postId,String start_tm) {
        HeldService.getService().holdPost(PreferenceHelper.getInstance(mActivity).readPreference("SESSION_TOKEN"),postId,start_tm,"" ,new Callback<HoldResponse>(){
        @Override
            public void success(HoldResponse holdResponse, Response response) {

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
}
