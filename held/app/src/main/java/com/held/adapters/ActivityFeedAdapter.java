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
import com.held.customview.PicassoCache;
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
                    .inflate(R.layout.row_activity_feed, parent, false);
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
            PicassoCache.getPicassoInstance(mActivity)
                    .load(mActivityFeedDataList.get(position).getPostPic())
                    .into(viewHolder.mPostImg);
            viewHolder.mActivityFeedDes.setText(mActivityFeedDataList.get(position).getOwner_display_name());
            viewHolder.mActivityFeedDesTime.setText(mActivityFeedDataList.get(position).getText());
            viewHolder.mActivityFeedTime.setText(mActivityFeedDataList.get(position).getDate());
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

        private ImageView mPostImg;
        private TextView mActivityFeedDes, mActivityFeedDesTime, mActivityFeedTime;

        public FeedViewHolder(View itemView) {
            super(itemView);
            mPostImg = (ImageView) itemView.findViewById(R.id.post_img);
            mActivityFeedDes = (TextView) itemView.findViewById(R.id.activity_feed_txt);
            mActivityFeedDesTime = (TextView) itemView.findViewById(R.id.feed_desc_time);
            mActivityFeedTime = (TextView) itemView.findViewById(R.id.activity_feed_time_txt);
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
