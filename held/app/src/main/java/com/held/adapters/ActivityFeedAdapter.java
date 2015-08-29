package com.held.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.retrofit.response.ActivityFeedData;

import java.util.List;

public class ActivityFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParentActivity mActivity;
    private List<ActivityFeedData> mActivityFeedDataList;
    private boolean mIsLastPage;

    private static final int TYPE_DATA = 0;
    private static final int TYPE_FOOTER = 1;


    public ActivityFeedAdapter(ParentActivity activity, List<ActivityFeedData> activityFeedDataList, boolean isLastPage) {
        mActivity = activity;
        mActivityFeedDataList = activityFeedDataList;
        mIsLastPage = isLastPage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_progress_bar, parent, false);
            return new ProgressViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_friends_list, parent, false);
            return new FeedViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProgressViewHolder) {
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
            FeedViewHolder viewHolder = (FeedViewHolder) holder;
            viewHolder.mUserName.setText(mActivityFeedDataList.get(position).getOwner_display_name());
            viewHolder.mUserDetail.setText(mActivityFeedDataList.get(position).getText());
            viewHolder.mDate.setText(mActivityFeedDataList.get(position).getDate());
        }
    }

    public void setActivityFeedList(List<ActivityFeedData> activityFeedList, boolean isLastPage) {
        mActivityFeedDataList = activityFeedList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == mActivityFeedDataList.size() ? TYPE_FOOTER : TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return mActivityFeedDataList.size() + 1;
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePic;
        private TextView mUserName, mUserDetail, mDate;

        public FeedViewHolder(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.FRIEND_profile_pic);
            mUserName = (TextView) itemView.findViewById(R.id.FRIEND_name);
            mUserDetail = (TextView) itemView.findViewById(R.id.FRIEND_description);
            mDate = (TextView) itemView.findViewById(R.id.FRIEND_time_txt);
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

}
