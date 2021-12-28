package com.example.app_dev_money_tracking;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

class Notification
{
    public static String CHANNEL_ID = "MoneyAppChannel";
    public static int notificationID = 1;

    //notification showing parameters when user gets Notification
    public static void ShowNotification(String title, String data,Class nextClass,int notificationID, Context ctx)
    {
        // Intent of activity that will be displayed on notification click
        Intent intent = new Intent(ctx, nextClass);
        // User needs to get back to previous activities, so the copy of back stack is needed
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addNextIntentWithParentStack(intent);

        Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.ic_wallet_icon_foreground);

        PendingIntent resultPendingIntent = stackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx,CHANNEL_ID)
                .setSmallIcon(R.drawable.piggybankk)
                .setContentTitle(title)
                .setContentText(data)
                .setLargeIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setColor(0x030F5A).
                        setVibrate(new long[] {200,100,200,100,200,100})
                .setLights(0x20B2AA, 3000, 1500).setColorized(true).
                        setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        //sending the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(notificationID, builder.build());
    }

}
