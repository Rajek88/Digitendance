package com.texuretechnologies.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());
    public static final int FLAG_ONGOING_EVENT = 2;

    private static final String TAG = "GeofenceBroadcastReceiv";
    private String body;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence Triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper=new NotificationHelper(context);
        GeofencingEvent geofencingEvent= GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving Geofence Event");
            return;
        }
        List<Geofence> geofenceList =geofencingEvent.getTriggeringGeofences();
        for(Geofence geofence:geofenceList){
            Log.d(TAG, "onReceive: "+geofence.getRequestId());
        }
        Location location=geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "Entered into Office premises", Toast.LENGTH_SHORT).show();
                Notification.Builder noti = new Notification.Builder(context)
                        .setContentTitle("You are now logged into office")
                        .setContentText("Your IN-Time is : "+currentDateandTime)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setOngoing(true);// Again, THIS is the important line;
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    String channelId = "some_channel_id";
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            "Running",
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                    noti.setChannelId(channelId);
                }
                notificationManager.notify(FLAG_ONGOING_EVENT, noti.build());
                notificationHelper.sendHighPriorityNotification("You have entered into Office Premises",body=" Welcome to Office",MainActivity.class);
                MainActivity g=new MainActivity();
                try {
                    g.inLogger();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION DWELLING", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("You are currently in Office Premises",body="Hardwork is the key of Success",MainActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION EXITED", Toast.LENGTH_SHORT).show();
                Notification.Builder noti1 = new Notification.Builder(context)
                        .setContentTitle("You are now logged out of office")
                        .setContentText("Your OUT-Time is : "+currentDateandTime)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setOngoing(true);// Again, THIS is the important line;
                NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    String channelId = "some_channel_id";
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            "Running",
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager1.createNotificationChannel(channel);
                    noti1.setChannelId(channelId);
                }
                // notificationId is a unique int for each notification that you must define
                notificationManager1.notify(FLAG_ONGOING_EVENT, noti1.build());
                notificationHelper.sendHighPriorityNotification("You have exited the Office Premises",body=" See you soon",MainActivity.class);
                MainActivity h=new MainActivity();
                try {
                    h.outLogger();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        Toast.makeText(context, currentDateandTime, Toast.LENGTH_SHORT).show();
    }
}
