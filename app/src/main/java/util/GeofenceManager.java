package util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import it.bsamu.sam.virtualgymbuddy.R;
import receiver.GymGeofenceBroadcastReceiver;

public class GeofenceManager {
    private static final String GYM_GEOFENCE_ID = "GYM_GEOFENCE";
    private static final int GYM_GEOFENCE_RADIUS_M = 10;
    private static final long GEOFENCE_EXPIRATION_MS = 1000000000;
    private static final int GEOFENCE_LOITERING_MS =  10000; //300000;

    public static boolean hasGeofenceBeenAdded(Context context) {
        return getGymGeofencePendingIntent(context, false) != null;
    }

    @SuppressLint("MissingPermission")
    public static void addGymGeofence(Context context) {
        /**
         * Adds a geofence relative to the location of user's gym,
         * as specified in their preferences
         */
        LatLng gymLocation = getGymLocation(context);

        if(gymLocation == null) {
            return;
        }

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);

        // build geofence
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GYM_GEOFENCE_ID)
                .setCircularRegion(
                        gymLocation.latitude,
                        gymLocation.longitude,
                        GYM_GEOFENCE_RADIUS_M
                )
                .setLoiteringDelay(GEOFENCE_LOITERING_MS)
                .setExpirationDuration(GEOFENCE_EXPIRATION_MS)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_DWELL|
                                Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build();

        // add geofence using the client
        geofencingClient.addGeofences(
                new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                        .addGeofence(geofence)
                        .build(),
                getGymGeofencePendingIntent(context, true)
        ).addOnSuccessListener((aVoid) -> {
            createNotificationChannel(context);
        }).addOnFailureListener((e) -> {
            e.printStackTrace();
        });
    }

    @Nullable
    public static LatLng getGymLocation(Context context) {
        /**
         * Returns a LatLng object describing the location of user's gym saved in
         * preferences, or null if user didn't specify a gym location
         */

        SharedPreferences prefs =
                context.getSharedPreferences(
                        context.getString(R.string.pref_file_key), Context.MODE_PRIVATE
                );

        double lat = prefs.getFloat(context.getString(R.string.gym_lat_key), 0);
        double lng = prefs.getFloat(context.getString(R.string.gym_lng_key), 0);

        return lat != 0 || lng != 0 ? // no gym selected (unless user trains at Null Island...)
                new LatLng(lat, lng) :
                null;
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = notificationManager
                    .getNotificationChannel(context.getString(R.string.notification_channel_id));

            if(notificationChannel == null) {
                CharSequence name = "NOTIFICATION_CHANNEL";
                String description = "Gym geofence notifications channel";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(
                        context.getString(R.string.notification_channel_id), name, importance
                );
                channel.setDescription(description);
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static PendingIntent getGymGeofencePendingIntent(Context context, boolean create) {
        /**
         * If `create` is false, will return null if the PendingIntent doesn't exist
         */
        Intent intent = new Intent(context, GymGeofenceBroadcastReceiver.class);
        PendingIntent geofencePendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // add mutability flag as required by versions of Android >= S
            geofencePendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent,
                    create ?
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT :
                            PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            geofencePendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent,
                    create ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_NO_CREATE
            );
        }
        return geofencePendingIntent;
    }

}
