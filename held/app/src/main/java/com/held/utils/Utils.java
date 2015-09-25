package com.held.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.fragment.ParentFragment;

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

    public static ParentFragment getCurrVisibleFragment(ParentActivity activity) {
        if (activity != null) {
            return (ParentFragment) activity.getSupportFragmentManager().findFragmentById(R.id.frag_container);
        }
        return null;
    }

}
