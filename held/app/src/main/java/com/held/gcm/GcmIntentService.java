package com.held.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.held.activity.R;
import com.held.activity.SplashActivity;

/**
 * Created by YMediaLabs on 18/2/15.
 */
public class GcmIntentService extends IntentService {


    private final String TAG = GcmIntentService.class.getSimpleName();
    private AudioManager am;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "Send error : " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "Deleted messages on server: " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                checkNotificationType(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        } else {
            Log.i(TAG, "Received Empty push notification message");
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void checkNotificationType(Bundle bundleResponse) {

        Intent intent = new Intent(this, SplashActivity.class);
        sendNotification(intent);

        //Coming Notification format
        // "GCM": "{\"data\":{\"title\":\"push notification\",\"message\":\"You have an Event coming up!
        // Don't forget to check your schedule!\",\"notificationId\":2,\"vibrate\":1,\"sound\":1}}"
//
//        String type = bundleResponse.getString("type");
//        String message = bundleResponse.getString("message");
//        String title = Utilities.getString(R.string.DASHBOARD_winning_seat);
//
//        String soundStr = bundleResponse.getString("sound");
//        String vibrateStr = bundleResponse.getString("vibrate");
//        boolean sound = !TextUtils.isEmpty(soundStr) && soundStr.matches("1");
//        boolean vibrate = !TextUtils.isEmpty(vibrateStr) && vibrateStr.matches("1");
//
//            int gameId, oppToken;
//
//            switch (launchScreen) {
//
//                case LAUNCH_EVENT_PICKER_SCREEN:
//                    Intent intent = new Intent(this, EventPickerActivity.class);
//                    intent.putExtra(Utilities.getString(R.string.Key_launch), AppConstants.AllSCREENS.LAUNCH_EVENT_PICKER_SCREEN.ID);
//                    sendNotification(intent, title, message, sound, vibrate);
//                    return;
//
//                case LAUNCH_DASHBOARD_SCREEN:
//                    intent = new Intent(this, DashboardActivity.class);
//                    intent.putExtra(Utilities.getString(R.string.Key_launch), AppConstants.AllSCREENS.LAUNCH_DASHBOARD_SCREEN.ID);
//                    sendNotification(intent, title, message, sound, vibrate);
//                    return;
//
//                case LAUNCH_SCHEDULE_MANAGER_SCREEN:
//                    intent = new Intent(this, ScheduleManagerActivity.class);
//                    intent.putExtra(Utilities.getString(R.string.Key_launch), AppConstants.AllSCREENS.LAUNCH_SCHEDULE_MANAGER_SCREEN.ID);
//                    sendNotification(intent, title, message, sound, vibrate);
//                    break;
//
//                case LAUNCH_LIVE_SEAT_SCREEN:
//                    String gameIdStr = bundleResponse.getString(Utilities.getString(R.string.GCM_GAME_ID));
//                    String oppTokenStr = bundleResponse.getString(Utilities.getString(R.string.GCM_OPP_TOKEN));
//
//                    if (TextUtils.isEmpty(gameIdStr) || !TextUtils.isDigitsOnly(gameIdStr))
//                        return;
//                    if (TextUtils.isEmpty(oppTokenStr) || !TextUtils.isDigitsOnly(oppTokenStr))
//                        return;
//
//                    gameId = Integer.parseInt(bundleResponse.getString(Utilities.getString(R.string.GCM_GAME_ID)));
//                    oppToken = Integer.parseInt(bundleResponse.getString(Utilities.getString(R.string.GCM_OPP_TOKEN)));
//
//                    intent = new Intent(this, LiveSeatActivity.class);
//                    intent.putExtra(Utilities.getString(R.string.Key_game_id), gameId);
//                    intent.putExtra(Utilities.getString(R.string.Key_opp_token), oppToken);
//                    intent.putExtra(Utilities.getString(R.string.Key_launch), AppConstants.AllSCREENS.LAUNCH_LIVE_SEAT_SCREEN.ID);
//
//                    // Launching LiveSeat directly if app is in foreground
//                    if (TWSApplication.IS_APP_FOREGROUND) {
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    } else {
//                        sendNotification(intent, title, message, sound, vibrate);
//                    }
//                    break;
//
//                case LAUNCH_COUPON_DETAIL:
//                    String couponIdStr = bundleResponse.getString(Utilities.getString(R.string.GCM_COUPON_ID));
//                    String walletItemIdStr = bundleResponse.getString(Utilities.getString(R.string.GCM_WALLET_ITEM_ID));
//
//                    if (TextUtils.isEmpty(couponIdStr) || !TextUtils.isDigitsOnly(couponIdStr))
//                        return;
//                    if (TextUtils.isEmpty(walletItemIdStr) || !TextUtils.isDigitsOnly(walletItemIdStr))
//                        return;
//
//                    int couponId = Integer.parseInt(couponIdStr);
//                    int walletItemId = Integer.parseInt(walletItemIdStr);
//
//                    intent = new Intent(this, WalletActivity.class);
//                    intent.putExtra(Utilities.getString(R.string.Key_launch), AppConstants.AllSCREENS.LAUNCH_COUPON_DETAIL.ID);
//                    intent.putExtra(Utilities.getString(R.string.Key_coupon_id), couponId);
//                    intent.putExtra(Utilities.getString(R.string.Key_wallet_item_id), walletItemId);
//                    sendNotification(intent, title, message, sound, vibrate);
//                    break;
//
//                case LAUNCH_EVENT_CLASH_SCREEN:
//                    String existingGameIdStr = bundleResponse.getString(Utilities.getString(R.string.Key_existing_event_id));
//                    String newGameIdStr = bundleResponse.getString(Utilities.getString(R.string.Key_existing_event_id));
//
//                    if (TextUtils.isEmpty(existingGameIdStr) || !TextUtils.isDigitsOnly(existingGameIdStr))
//                        return;
//                    if (TextUtils.isEmpty(newGameIdStr) || !TextUtils.isDigitsOnly(newGameIdStr))
//                        return;
//
//                    int existingGameId = Integer.parseInt(existingGameIdStr);
//                    int newGameId = Integer.parseInt(newGameIdStr);
//
//                    intent = new Intent(this, EventPickerActivity.class);
//                    intent.putExtra(Utilities.getString(R.string.Key_launch), AppConstants.AllSCREENS.LAUNCH_EVENT_CLASH_SCREEN.ID);
//                    intent.putExtra(Utilities.getString(R.string.Key_existing_event_id), existingGameId);
//                    intent.putExtra(Utilities.getString(R.string.Key_new_event_id), newGameId);
//                    sendNotification(intent, title, message, sound, vibrate);
//                    break;
//            }
//        } else {
//            Logger.d(TAG, "Coming push notification message without Notification Id : " + bundleResponse.toString());
//        }
    }

    protected void sendNotification(Intent intent) {

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(GCMConstants.NotificationID.getID(), mBuilder.build());
    }

    /*public void setVibrate(NotificationCompat.Builder mBuilder) {
        if (am != null) {
            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    break;
            }
        }
    }*/
}