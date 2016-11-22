package com.example.manishchoudhary.rightnavigationapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by manish.choudhary on 11/22/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, NotificationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        String like = intent.getStringExtra("Like");

        if(like.equals("Like")){
            Notification notification = builder.setContentTitle("App Notification")
                    .setContentText("Liked status of " + intent.getStringExtra("Name"))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }

        if(like.equals("Unlike")){
            Notification notification = builder.setContentTitle("Demo App Notification")
                    .setContentText("Unliked status of " + intent.getStringExtra("Name"))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
    }
}
