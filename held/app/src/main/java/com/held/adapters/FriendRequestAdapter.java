package com.held.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ApproveFriendResponse;
import com.held.retrofit.response.DeclineFriendResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private PostActivity mActivity;
    private List<SearchUserResponse> mFriendRequestList;

    public FriendRequestAdapter(PostActivity activity, List<SearchUserResponse> friendRequestList) {
        mActivity = activity;
        mFriendRequestList = friendRequestList;
    }

    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.row_friend_request, parent, false);
        return new FriendRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, final int position) {
        Picasso.with(mActivity).load(AppConstants.BASE_URL + mFriendRequestList.get(position).getPic()).into(holder.mProfileImg);
        holder.mUserNameTxt.setText(mFriendRequestList.get(position).getDisplay_name());
        holder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAccepFriendRequestApi(mFriendRequestList.get(position).getDisplay_name());
            }
        });
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDeleteFriendRequestApi(mFriendRequestList.get(position).getDisplay_name());
            }
        });
    }

    private void callDeleteFriendRequestApi(String name) {//PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_session_token))
        HeldService.getService().declineFriend(PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_session_token)),
                name, new Callback<DeclineFriendResponse>() {
                    @Override
                    public void success(DeclineFriendResponse declineFriendResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void callAccepFriendRequestApi(String name) {
        HeldService.getService().approveFriend(PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_session_token)),
                name, new Callback<ApproveFriendResponse>() {
                    @Override
                    public void success(ApproveFriendResponse approveFriendResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return mFriendRequestList.size();
    }

    public void setFriendRequestList(List<SearchUserResponse> friendRequestList) {
        mFriendRequestList = friendRequestList;
        notifyDataSetChanged();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        ImageView mProfileImg;
        TextView mUserNameTxt;
        Button mAcceptBtn, mDeleteBtn;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            mProfileImg = (ImageView) itemView.findViewById(R.id.RFR_user_img);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.RFR_user_name_txt);
            mAcceptBtn = (Button) itemView.findViewById(R.id.RFR_accept_btn);
            mDeleteBtn = (Button) itemView.findViewById(R.id.RFR_delete_btn);
        }
    }
}
