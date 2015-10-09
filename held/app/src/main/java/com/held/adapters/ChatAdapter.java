package com.held.adapters;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.ChatActivity;
import com.held.activity.InboxActivity;
import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.User;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_LEFT = 0;
    private static final int ITEM_RIGHT = 1;


    private ParentActivity mActivity;
    private List<PostChatData> mPostChatData;
    private int lastPosition = -1;
    private GestureDetector mGestureDetector;
    private boolean delayEnterAnimation = true, animationsLocked,mIsLastPage;
    private PreferenceHelper mPreference;
    private User currentUser=null,friendUser=null;

    public ChatAdapter(ParentActivity activity, List<PostChatData> postChatData) {
        mActivity = activity;
        mPostChatData = postChatData;
        mPreference = PreferenceHelper.getInstance(mActivity);
    }


    @Override
    public int getItemViewType(int position) {
        Timber.d("trying to get view type");
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        int type;
        if(mPostChatData.get(position).getUser()==null) {
            type = (mPostChatData.get(position).getFromUser().getDisplayName()).
                    equals(mPreference.readPreference(Utils.getString(R.string.API_user_name))) ? ITEM_RIGHT : ITEM_LEFT;
        }else {
            type = (mPostChatData.get(position).getUser().getDisplayName()).
                    equals(mPreference.readPreference(Utils.getString(R.string.API_user_name))) ? ITEM_RIGHT : ITEM_LEFT;
        }
        Timber.d("chat id " + position + " is type " + type);
        return type;

    }

    @Override
    public int getItemCount() {
        return mPostChatData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Timber.d(" creating viewholder ");
        View v;
//        switch (viewType) {
//            case 0:
        if (viewType == ITEM_RIGHT) {
            Timber.d("returning viewholder 0");
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_right, parent, false);
            return new ViewHolder0(v);
        } else if(viewType == ITEM_LEFT) {
            Timber.d("returning viewholder 2");
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_left, parent, false);
            return new ViewHolder2(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Timber.d("onBind view holder");
        ///Get Current user
        String userName=mPreference.readPreference(Utils.getString(R.string.API_user_name));
        if(mPostChatData.get(position).getUser()==null) {
            if (userName.equals(mPostChatData.get(position).getFromUser().getDisplayName())) {
                currentUser = mPostChatData.get(position).getFromUser();
                friendUser = mPostChatData.get(position).getToUser();
            } else if (userName.equals(mPostChatData.get(position).getToUser().getDisplayName()) && mPostChatData.get(position).getUser() == null) {
                currentUser = mPostChatData.get(position).getToUser();
                friendUser = mPostChatData.get(position).getFromUser();
            }
        }
        else {
            if (userName.equals(mPostChatData.get(position).getUser().getDisplayName()))
            {
                currentUser = mPostChatData.get(position).getUser();
            } else if(!userName.equals(mPostChatData.get(position).getUser().getDisplayName())) {
                friendUser = mPostChatData.get(position).getUser();
            }
        }


        if (holder instanceof ViewHolder0) {
            Timber.d("populating viewholder 0");

            ViewHolder0 viewHolder0 = (ViewHolder0) holder;
            viewHolder0.mUserNameTxt.setText(currentUser.getDisplayName());
            String ts = mPostChatData.get(position).getDate();
            viewHolder0.mDateTxt.setText(Utils.convertDate(ts));
            viewHolder0.mDesTxt.setText(mPostChatData.get(position).getText());
            Picasso.with(mActivity).load(AppConstants.BASE_URL + currentUser.getProfilePic()).into(viewHolder0.mProfilePic);
        } else if(holder instanceof ViewHolder2) {


            ViewHolder2 viewHolder = (ViewHolder2) holder;
            viewHolder.mUserNameTxt.setText(friendUser.getDisplayName());
            String ts = mPostChatData.get(position).getDate();
            viewHolder.mDateTxt.setText(Utils.convertDate(ts));
            viewHolder.mDesTxt.setText(mPostChatData.get(position).getText());
            Picasso.with(mActivity).load(AppConstants.BASE_URL + friendUser.getProfilePic()).into(viewHolder.mProfilePic);
        }
        runEnterAnimation(holder.itemView, position);
    }


    public void setPostChats(List<PostChatData> postChatData) {
        mPostChatData.clear();
        mPostChatData=postChatData;
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

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastPosition) {
            lastPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 50 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = false;
                        }
                    })
                    .start();
        }
    }
}