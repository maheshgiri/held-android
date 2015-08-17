package com.held.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jay on 1/8/15.
 */
public class Utils {

    public static String getString(int resId) {
        return HeldApplication.getAppContext().getString(resId);
    }

    public static Drawable getDrawable(int resId) {
        //noinspection deprecation
        return HeldApplication.getAppContext().getResources().getDrawable(resId);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
//        View focusView = activity.getCurrentFocus();
////        if (focusView != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

}
