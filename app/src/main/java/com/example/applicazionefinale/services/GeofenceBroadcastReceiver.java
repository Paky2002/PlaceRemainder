package com.example.applicazionefinale.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.applicazionefinale.MainActivity;
import com.example.applicazionefinale.helper.NotificationHelper;
import com.example.applicazionefinale.persistence.ContactsManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    private ContactsManager contactManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            String nome = contactManager.getInstance(context.getApplicationContext()).getContactById(Integer.parseInt(geofence.getRequestId())).getNome();
            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Toast.makeText(context, "SEI VICINO A ", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("SEO VICINO A QUALCUNO", nome, MainActivity.class);
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MainActivity.class);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MainActivity.class);
                    break;
            }
        }
//        Location location = geofencingEvent.getTriggeringLocation();




    }
}