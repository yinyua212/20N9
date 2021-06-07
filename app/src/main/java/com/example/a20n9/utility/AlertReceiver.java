package com.example.a20n9.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.os.IResultReceiver;

import com.example.a20n9.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.redheart))
                .setContentTitle("20N9")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        Notification notification = builder.build();
        notificationManager.cancel(0); // 移除id值為0的通知
        notificationManager.notify(0, notification);

    }
}
