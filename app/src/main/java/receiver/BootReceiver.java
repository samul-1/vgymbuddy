package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import util.GeofenceManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // add geofences at boot-up time
        GeofenceManager.addGymGeofence(context);
    }
}
