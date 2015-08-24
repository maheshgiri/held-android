package com.held.utils;


import android.app.Activity;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.customview.ProgressDialog;

public class DialogUtils {

    private static ProgressDialog mProgressDialog;

    public static void resetDialog(ParentActivity activity) {
        mProgressDialog = new ProgressDialog(activity);
    }

    public static void showProgressBar() {
        if (mProgressDialog == null) {
            new Exception(HeldApplication.getAppContext().getString(R.string.error_progress_dialog_null_call_reset))
                    .printStackTrace();
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public static void stopProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
