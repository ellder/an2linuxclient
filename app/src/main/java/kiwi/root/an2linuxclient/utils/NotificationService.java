/*
 * Copyright 2017 rootkiwi
 *
 * AN2Linux-client is licensed under GNU General Public License 3.
 *
 * See LICENSE for more details.
 */

package kiwi.root.an2linuxclient.utils;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
//import android.os.Bundle;
//import android.util.Log;

import kiwi.root.an2linuxclient.network.NotificationHandler;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //logDebug(sbn);
        boolean globalEnabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("preference_enable_an2linux", false);
        boolean appEnabled = getSharedPreferences("enabled_applications", MODE_PRIVATE).getBoolean(sbn.getPackageName(), false);

        if (globalEnabled && appEnabled) {
            SharedPreferences sharedPrefs = getSharedPreferences("notification_settings_custom", MODE_PRIVATE);
            boolean usingCustomSettings = sharedPrefs.getBoolean(sbn.getPackageName() + "_preference_use_custom_settings", false);
            if (!usingCustomSettings) {
                sharedPrefs = getSharedPreferences("notification_settings_global", MODE_PRIVATE);
            }

            int flags = sbn.getNotification().flags;
            boolean blockOngoing = sharedPrefs.getBoolean(getCorrectPrefKey(
                    "preference_block_ongoing", sbn.getPackageName(), usingCustomSettings), false);
            if (blockOngoing && (flags & Notification.FLAG_ONGOING_EVENT) != 0){
                return;
            }

            boolean blockForeground = sharedPrefs.getBoolean(getCorrectPrefKey(
                    "preference_block_foreground", sbn.getPackageName(), usingCustomSettings), false);
            if (blockForeground && (flags & Notification.FLAG_FOREGROUND_SERVICE) != 0){
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
                boolean blockGroup = sharedPrefs.getBoolean(getCorrectPrefKey(
                        "preference_block_group", sbn.getPackageName(), usingCustomSettings), false);
                if (blockGroup && (flags & Notification.FLAG_GROUP_SUMMARY) != 0){
                    return;
                }
                boolean blockLocal = sharedPrefs.getBoolean(getCorrectPrefKey(
                        "preference_block_local", sbn.getPackageName(), usingCustomSettings), false);
                if (blockLocal && (flags & Notification.FLAG_LOCAL_ONLY) != 0){
                    return;
                }
            }

            NotificationHandler.handleStatusBarNotification(sbn, getApplicationContext());
        }
    }

    private String getCorrectPrefKey(String key, String packageName, boolean usingCustomSettings) {
        return usingCustomSettings ? packageName + "_" + key : key;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    /*private void logDebug(StatusBarNotification sbn){
        Log.d("<<<NEW_NOTIFICATION>>>", "<<<" + sbn.getPackageName() + ">>>");
        Bundle extras = sbn.getNotification().extras;

        if (extras.getCharSequence(android.app.Notification.EXTRA_TITLE) != null){
            Log.d("contentTitle", extras.getCharSequence(android.app.Notification.EXTRA_TITLE).toString());
        } else {
            Log.d("contentTitle", "null");
        }

        if (extras.getCharSequence(android.app.Notification.EXTRA_TEXT) != null){
            Log.d("contentText", extras.getCharSequence(android.app.Notification.EXTRA_TEXT).toString());
        } else {
            Log.d("contentText", "null");
        }

        if (extras.getCharSequence(android.app.Notification.EXTRA_SUB_TEXT) != null){
            Log.d("subText", extras.getCharSequence(android.app.Notification.EXTRA_SUB_TEXT).toString());
        } else {
            Log.d("subText", "null");
        }

        if (sbn.getNotification().tickerText != null){
            Log.d("tickerText", sbn.getNotification().tickerText.toString());
        }
        else {
            Log.d("tickerText", "null");
        }

        if (extras.getCharSequence(android.app.Notification.EXTRA_SUMMARY_TEXT) != null){
            Log.d("summaryText", extras.getCharSequence(android.app.Notification.EXTRA_SUMMARY_TEXT).toString());
        } else {
            Log.d("summaryText", "null");
        }

        Log.d("flags", String.valueOf(sbn.getNotification().flags));
        Log.d("FLAG_AUTO_CANCEL", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_AUTO_CANCEL) == 16));
        Log.d("FLAG_FOREGROUND_SERVICE", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_FOREGROUND_SERVICE) == 64));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
            Log.d("FLAG_GROUP_SUMMARY", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_GROUP_SUMMARY) == 512));
            Log.d("FLAG_LOCAL_ONLY", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_LOCAL_ONLY) == 256));
        }
        Log.d("FLAG_HIGH_PRIORITY", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_HIGH_PRIORITY) == 128));
        Log.d("priority", String.valueOf(sbn.getNotification().priority));
        Log.d("FLAG_INSISTENT", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_INSISTENT) == 4));
        Log.d("FLAG_NO_CLEAR", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_NO_CLEAR) == 32));
        Log.d("FLAG_ONGOING_EVENT", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_ONGOING_EVENT) == 2));
        Log.d("FLAG_ONLY_ALERT_ONCE", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_ONLY_ALERT_ONCE) == 8));
        Log.d("FLAG_SHOW_LIGHTS", String.valueOf((sbn.getNotification().flags & android.app.Notification.FLAG_SHOW_LIGHTS) == 1));
        Log.d("<<<END_NOTIFICATION>>>", "<<<" + sbn.getPackageName() + ">>>");
    }*/

}
