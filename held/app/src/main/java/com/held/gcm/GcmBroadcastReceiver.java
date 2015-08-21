package com.held.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;



/**
 * Created by YMediaLabs on 18/2/15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = GcmBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // If user has uninstalled-installed the app.
        // i.e. we were unable to call logout API but user is actually logged out.
//        if (TextUtils.isEmpty(PreferenceHelper.getSessionToken())) return;

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}