package com.held.gcm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.utils.PreferenceHelper;

import java.io.IOException;

import timber.log.Timber;


public class GCMControlManager {

    private static final String TAG = GCMControlManager.class.getSimpleName();

    private final ParentActivity mActivity;
    private GoogleCloudMessaging gcm;
    private String mRegistrationId;

    public GCMControlManager(ParentActivity activity) {
        mActivity = activity;
    }

    private String getRegistrationIdFromPrefs(Context context) {
        String registrationId = PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_registration_key));


        if (registrationId.isEmpty()) {
//            Logger.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
//        int registeredVersion = prefs.getInt(GCMConstants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int registeredVersion = PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_app_version), 0);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
//            Logger.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    /*private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return mActivity.getSharedPreferences(MessageActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }*/

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, 9000).show();
            } else {
//                Logger.i(TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mActivity);
                    }
                    mRegistrationId = gcm.register(mActivity.getString(R.string.sender_id));
                    msg = "Device registered, registration ID=" + mRegistrationId;
                    PreferenceHelper.getInstance(mActivity).writePreference(mActivity.getString(R.string.API_gcm_registration_key), mRegistrationId);
                    Timber.i("@@@@GCM ID :"+PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_gcm_registration_key)));
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    storeRegistrationIdIntoPrefs(mActivity, mRegistrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "\n" + "Registration ID : " + msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * storeRegistrationIdIntoPrefs
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */

    private void storeRegistrationIdIntoPrefs(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        PreferenceHelper.getInstance(mActivity).writePreference(mActivity.getString(R.string.API_gcm_registration_key), regId);
        PreferenceHelper.getInstance(mActivity).writePreference(mActivity.getString(R.string.API_app_version), appVersion);
    }

    public void setupGCM() {

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(mActivity);
            mRegistrationId = getRegistrationIdFromPrefs(mActivity);
            Log.d(TAG, "mRegistrationId : " + mRegistrationId);
//            if (mRegistrationId.isEmpty()) {
                registerInBackground();
//            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }
}
