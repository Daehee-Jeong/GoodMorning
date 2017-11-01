package com.kosta148.team1.goodmorning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.getBoolean("swt", false)) {

            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher).setTicker("샘플입니다").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("굿모닝~").setContentText("알람 시간입니다")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

            notificationmanager.notify(1, builder.build());
        }
/*
        RemoteViews contentiew = new RemoteViews(context.getPackageName(), R.layout.notification);
        contentiew.setOnClickPendingIntent(R.id.alarmBtn, pendingIntent);
        noti.contentView = contentiew;
        notificationManager.notify(1, noti);
*/
    }
}
