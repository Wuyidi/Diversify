package com.techhawk.diversify.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.techhawk.diversify.R;
import com.techhawk.diversify.activity.MainActivity;

/**
 * Created by Yidi Wu on 8/5/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager manager;


    @Override
    public void onReceive(Context context, Intent intent) {


        manager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        Intent playIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Diversify").setContentText("Today you have a event to attend.").setSmallIcon(R.drawable.ic_warning).setDefaults(Notification.DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true);
        manager.notify(1, builder.build());
    }
}
