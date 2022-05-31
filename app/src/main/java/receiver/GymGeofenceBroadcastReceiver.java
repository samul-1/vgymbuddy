package receiver;

import static android.provider.Settings.System.getString;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;
import java.util.List;

import it.bsamu.sam.virtualgymbuddy.MainActivity;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.AppDb;
import relational.entities.GymTransition;

public class GymGeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("GEOFENCE-ERROR", errorMessage);
            return;
        }

        // get transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            boolean isDwellTransition = geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL;

            // send notification and log the transition details to db
            logTransitionToDb(isDwellTransition, context);
            sendNotification(isDwellTransition, context);
        }
    }

    private void logTransitionToDb(boolean isDwell, Context context) {
        GymTransition transition = new GymTransition(new Date(), isDwell);
        AppDb db = AppDb.getInstance(context);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.gymTransitionDao().insertGymTransition(transition);
                return null;
            }
        }.execute();
    }


    private void sendNotification(boolean isDwell, Context context) {
        String notificationText = isDwell ? context.getString(R.string.start_working_out) :
                context.getString(R.string.total_gym_time);


        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_baseline_fitness_center_24)
                .setContentTitle(isDwell ?
                        context.getString(R.string.arrived_at_gym) :
                        context.getString(R.string.left_the_gym))
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                );

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
    }

}
