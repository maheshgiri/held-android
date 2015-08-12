package com.held.utils;


import android.app.Activity;

import com.held.customview.ProgressDialog;

public class DialogUtils {

    private static ProgressDialog mProgressDialog;

    public static void resetDialog(Activity activity) {
        mProgressDialog = new ProgressDialog(activity);
    }

    public static void showProgressBar() {
        mProgressDialog.show();
    }

    public static void stopProgressDialog() {
        mProgressDialog.dismiss();
    }

}
