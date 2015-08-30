package com.held.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.ChatActivity;
import com.held.activity.R;
import com.held.retrofit.response.PostChatData;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_LEFT = 0;
    private static final int ITEM_RIGHT = 1;


    private ChatActivity mActivity;
    private List<PostChatData> mPostChatData;
    private int lastPosition = -1;

    public ChatAdapter(ChatActivity activity, List<PostChatData> postChatData) {
        mActivity = activity;
        mPostChatData = postChatData;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return (mPostChatData.get(position).getOwner_display_name()).
                equals(PreferenceHelper.getInstance(mActivity).readPreference(Utils.getString(R.string.API_user_name))) ? ITEM_RIGHT : ITEM_LEFT;
    }

    @Override
    public int getItemCount() {
        return mPostChatData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
//        switch (viewType) {
//            case 0:
        if (viewType == ITEM_RIGHT) {
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_right, parent, false);
            return new ViewHolder0(v);
        } else {
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_left, parent, false);
            return new ViewHolder2(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder0) {
            ViewHolder0 viewHolder0 = (ViewHolder0) holder;
            viewHolder0.mUserNameTxt.setText(mPostChatData.get(position).getOwner_display_name());
            viewHolder0.mDateTxt.setText(mPostChatData.get(position).getDate());
            viewHolder0.mDesTxt.setText(mPostChatData.get(position).getMessage());
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mPostChatData.get(position).getOwner_pic()).into(viewHolder0.mProfilePic);
        } else {
            ViewHolder2 viewHolder = (ViewHolder2) holder;
            viewHolder.mUserNameTxt.setText(mPostChatData.get(position).getOwner_display_name());
            viewHolder.mDateTxt.setText(mPostChatData.get(position).getDate());
            viewHolder.mDesTxt.setText(mPostChatData.get(position).getMessage());
        }
    }

    public void setPostChats(List<PostChatData> postChatData) {
        mPostChatData = postChatData;
        notifyDataSetChanged();
//        notifyItemInserted(mPostChatData.size());
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView mUserNameTxt, mDesTxt, mDateTxt;
        private ImageView mProfilePic;

        public ViewHolder0(View itemView) {
            super(itemView);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.CHATRIGHT_user_name_txt);
            mDesTxt = (TextView) itemView.findViewById(R.id.CHATRIGHT_des_txt);
            mDateTxt = (TextView) itemView.findViewById(R.id.CHATRIGHT_date_txt);
            mProfilePic = (ImageView) itemView.findViewById(R.id.CHATRIGHT_profile_img);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView mUserNameTxt, mDesTxt, mDateTxt;
        private ImageView mProfilePic;

        public ViewHolder2(View itemView) {
            super(itemView);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.CHATLEFT_user_name_txt);
            mDesTxt = (TextView) itemView.findViewById(R.id.CHATLEFT_des_txt);
            mDateTxt = (TextView) itemView.findViewById(R.id.CHATLEFT_date_txt);
            mProfilePic = (ImageView) itemView.findViewById(R.id.CHATLEFT_profile_img);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}