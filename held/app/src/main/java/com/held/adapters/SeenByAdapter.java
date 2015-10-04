package com.held.adapters;

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
import com.held.utils.AppConstants;
import com.held.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAHESH on 10/3/2015.
 */
public class SeenByAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParentActivity mActivity;
   // private List<String[]> mActivityDataList;
    private boolean mIsLastPage;
    private static final int TYPE_DATA = 0;
    private static final int TYPE_FOOTER = 1;
    String musers[][];
    String userName,img;
    private ArrayList<String[]> mList;






    public SeenByAdapter(ParentActivity activity,String user,String imgUrl, boolean isLastPage){
        mActivity = activity;
        img=imgUrl;
        userName=user;
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
                    .inflate(R.layout.row_seenby, parent, false);
            return new SeenByViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

       // user[0]=mActivityDataList.get(position).toString();

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

            SeenByViewHolder viewHolder = (SeenByViewHolder) holder;
            viewHolder.mUserName.setText(userName);
            PicassoCache.getPicassoInstance(mActivity)
                    .load(AppConstants.BASE_URL + img)
                    .placeholder(R.drawable.user_icon)
                    .into(viewHolder.mProfilePic);

            viewHolder.mButton.setText("Request Sent");
             /*setColor(viewHolder.mButton);
            if(viewHolder.mButton.getText().equals("Add as Freiend")){
                viewHolder.mButton.setBackgroundColor(g(R.color.positve_btn)));
                viewHolder.mButton.setTextColor(Integer.parseInt(Utils.getString(R.color.white)));
            }else if(viewHolder.mButton.getText().equals("Freiends")){
                viewHolder.mButton.setBackgroundColor(Integer.parseInt(Utils.getString(R.drawable.button_background)));
                viewHolder.mButton.setTextColor(Integer.parseInt(Utils.getString(R.color.friend_btn_color)));
            }else if(viewHolder.mButton.getText().equals("Request Sent")){
                viewHolder.mButton.setBackgroundColor(Integer.parseInt(Utils.getString(R.color.friend_btn_color)));
                viewHolder.mButton.setTextColor(Integer.parseInt(Utils.getString(R.color.white)));
            }*/


        }
    }

    @Override
    public int getItemCount() {
       // return mList.size()+1;
        //mActivityDataList.size()+1;
        return 1;
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
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView mIndicationTxt;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mIndicationTxt = (TextView) v.findViewById(R.id.indication_txt);
        }
    }
    public void setColor(Button btn){
        if(btn.getText().equals("Add as Freiend")){
            btn.setBackgroundColor(Integer.parseInt(Utils.getString(R.color.positve_btn)));
            btn.setTextColor(Integer.parseInt(Utils.getString(R.color.white)));
        }else if(btn.getText().equals("Freiends")){
            btn.setBackgroundColor(Integer.parseInt(Utils.getString(R.drawable.button_background)));
            btn.setTextColor(Integer.parseInt(Utils.getString(R.color.friend_btn_color)));
        }else if(btn.getText().equals("Request Sent")){
            btn.setBackgroundColor(Integer.parseInt(Utils.getString(R.color.friend_btn_color)));
            btn.setTextColor(Integer.parseInt(Utils.getString(R.color.white)));
        }


    }




}
