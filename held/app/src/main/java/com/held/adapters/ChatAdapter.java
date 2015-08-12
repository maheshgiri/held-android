package com.held.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.retrofit.response.PostChatData;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private PostActivity mActivity;
    private List<PostChatData> mPostChatData;

    public ChatAdapter(PostActivity activity, List<PostChatData> postChatData) {
        mActivity = activity;
        mPostChatData = postChatData;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
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
        if (mPostChatData.get(viewType).getOwner_display_name().
                equals(PreferenceHelper.getInstance(mActivity).readPreference(Utils.getString(R.string.API_user_name)))) {
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_right, parent, false);
            return new ViewHolder0(v);
        } else {
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_left, parent, false);
            return new ViewHolder2(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mPostChatData.get(position).getOwner_display_name().equals(PreferenceHelper.getInstance(mActivity).readPreference(Utils.getString(R.string.API_user_name)))) {
            ViewHolder0 viewHolder0 = (ViewHolder0) holder;
            viewHolder0.mUserNameTxt.setText(mPostChatData.get(position).getOwner_display_name());
            viewHolder0.mDateTxt.setText(mPostChatData.get(position).getDate());
            viewHolder0.mDesTxt.setText(mPostChatData.get(position).getMessage());
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

}