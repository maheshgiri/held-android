package com.held.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.customview.PicassoCache;
import com.held.retrofit.response.SeenByData;
import com.held.utils.AppConstants;
import com.held.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by MAHESH on 10/3/2015.
 */
public class SeenByAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParentActivity mActivity;
    private boolean mIsLastPage;



    public SeenByAdapter(ParentActivity activity,String user,String imgUrl, boolean isLastPage){
        mActivity = activity;
        mIsLastPage = isLastPage;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_seenby, parent, false);
        Timber.d("created seenby view holder");
        return new SeenByViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String userName = null,img = null;
        String requestStatus =null;

        Timber.d("on bind viewholder");
        SeenByViewHolder viewHolder = (SeenByViewHolder) holder;
        switch(position){
            case 0:
                userName = "swapnil3";
                img = "/user_thumbnails/swapnil3_1443690679233.jpg";
                requestStatus = "Add as Friend";
                viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.new_btn_color));
                break;
            case 1:
                userName = "vinay123";
                img = "/user_images/swapnil_1443349455353.jpg";
                requestStatus = "Friends";
                viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                viewHolder.mButton.setTextColor(mActivity.getResources().getColor(R.color.friend_btn_color));
                break;
            case 2:
                userName = "swapnil4";
                img = "/user_thumbnails/vinay123_1443851725451.jpg";
                requestStatus = "Request Sent";
                viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.friend_req_color));
                break;

        }
        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        viewHolder.mUserName.setTypeface(medium);
        viewHolder.mUserName.setText(userName);
        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + img)
                .placeholder(R.drawable.user_icon)
                .into(viewHolder.mProfilePic);

        viewHolder.mButton.setText(requestStatus);


    }

    @Override
    public int getItemCount() {
       //todo: fix later
        return 3;
    }



    /*public void setActivityFeedList(List<> activitySeenList, boolean isLastPage) {
        //mActivityDataList = activitySeenList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }*/
    public static class SeenByViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePic;
        private TextView mUserName;
        private Button mButton;

        public SeenByViewHolder(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.profile_img);
            mUserName = (TextView) itemView.findViewById(R.id.user_name_txt);
            mButton = (Button) itemView.findViewById(R.id.button);

        }
    }

    public void setColor(Button btn){
        if(btn.getText().equals("Add as Friend")){
            btn.setBackgroundColor(Integer.parseInt(Utils.getString(R.color.positve_btn)));
            btn.setTextColor(Integer.parseInt(Utils.getString(R.color.white)));
        }else if(btn.getText().equals("Friends")){
            btn.setBackgroundColor(Integer.parseInt(Utils.getString(R.drawable.button_background)));
            btn.setTextColor(Integer.parseInt(Utils.getString(R.color.friend_btn_color)));
        }else if(btn.getText().equals("Request Sent")){
            btn.setBackgroundColor(Integer.parseInt(Utils.getString(R.color.friend_btn_color)));
            btn.setTextColor(Integer.parseInt(Utils.getString(R.color.white)));
        }


    }




}
