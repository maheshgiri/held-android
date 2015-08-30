package com.held.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.held.activity.NotificationActivity;
import com.held.activity.R;
import com.held.fragment.DownloadRequestFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ApproveDownloadResponse;
import com.held.retrofit.response.DeclineDownloadResponse;
import com.held.retrofit.response.DownloadRequestData;
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

public class DownloadRequestAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private NotificationActivity mActivity;
    private List<DownloadRequestData> mDownloadRequestList;
    private boolean mIsLastPage = true;
    private DownloadRequestFragment mDownloadRequestFragment;

    public DownloadRequestAdapter(NotificationActivity activity, List<DownloadRequestData> DownloadRequestList, boolean isLastPage, DownloadRequestFragment downloadRequestFragment) {
        mActivity = activity;
        mDownloadRequestList = DownloadRequestList;
        mIsLastPage = isLastPage;
        mDownloadRequestFragment = downloadRequestFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.row_friend_request, parent, false);
            return new DownloadRequestViewHolder(v);
        } else {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_progress_bar, parent, false);
            return new ProgressViewHolder(v);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DownloadRequestViewHolder) {

            DownloadRequestViewHolder viewHolder = (DownloadRequestViewHolder) holder;

            Picasso.with(mActivity).load(AppConstants.BASE_URL + mDownloadRequestList.get(position).getOwner_pic()).into(viewHolder.mProfileImg);
            viewHolder.mUserNameTxt.setText(mDownloadRequestList.get(position).getOwner_display_name());
            viewHolder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity.getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callApproveDownloadApi(mDownloadRequestList.get(position).getRid());
                    } else {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                    }
                }
            });
            viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity.getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callDeclineDownloadApi(mDownloadRequestList.get(position).getRid());
                    } else {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                    }
                }
            });
        } else {
            ProgressViewHolder viewHolder = (ProgressViewHolder) holder;
            if (mIsLastPage) {
                viewHolder.mIndicationTxt.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
            } else {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.mIndicationTxt.setVisibility(View.GONE);
                viewHolder.progressBar.setIndeterminate(true);
            }

        }
    }

    private void callDeclineDownloadApi(String requestId) {
        HeldService.getService().declineDownloadRequest(PreferenceHelper.getInstance(mActivity)
                .readPreference(mActivity.getString(R.string.API_session_token)), requestId, new Callback<DeclineDownloadResponse>() {
            @Override
            public void success(DeclineDownloadResponse declineDownloadResponse, Response response) {
                DialogUtils.stopProgressDialog();

            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
            }
        });
    }

    private void callApproveDownloadApi(String requestId) {
        HeldService.getService().approveDownloadRequest(PreferenceHelper.getInstance(mActivity)
                .readPreference(mActivity.getString(R.string.API_session_token)), requestId, new Callback<ApproveDownloadResponse>() {
            @Override
            public void success(ApproveDownloadResponse approveDownloadResponse, Response response) {
                DialogUtils.stopProgressDialog();
                mDownloadRequestList.clear();
                long start = System.currentTimeMillis();
                mDownloadRequestFragment.callDownloadRequestListApi(start);
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDownloadRequestList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mDownloadRequestList.size() == position ? TYPE_FOOTER : TYPE_ITEM;
    }

    public void setDownloadRequestList(List<DownloadRequestData> downloadRequestList, boolean isLastPage) {
        mDownloadRequestList = downloadRequestList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    public static class DownloadRequestViewHolder extends RecyclerView.ViewHolder {

        ImageView mProfileImg;
        TextView mUserNameTxt;
        Button mAcceptBtn, mDeleteBtn;

        public DownloadRequestViewHolder(View itemView) {
            super(itemView);
            mProfileImg = (ImageView) itemView.findViewById(R.id.RFR_user_img);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.RFR_user_name_txt);
            mAcceptBtn = (Button) itemView.findViewById(R.id.RFR_accept_btn);
            mDeleteBtn = (Button) itemView.findViewById(R.id.RFR_delete_btn);
        }
    }
}