package it.bsamu.sam.virtualgymbuddy;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import adapter.NavigationAdapter;
import it.bsamu.sam.virtualgymbuddy.databinding.ActivityMainBinding;
import receiver.GymGeofenceBroadcastReceiver;
import relational.AppDb;
import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingProgram;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    Button gymMapBtn;

    private AppDb db;

    private GeofencingClient geofencingClient;
    private final String GYM_GEOFENCE_ID = "GYM_GEOFENCE";
    private final int GYM_GEOFENCE_RADIUS_M = 10;
    private final long GEOFENCE_EXPIRATION_MS = 1000000000;
    private final int GEOFENCE_LOITERING_MS =  10000; //300000;
    private PendingIntent geofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gymMapBtn = findViewById(R.id.select_gym_location_btn);
        //gymMapBtn.setOnClickListener((__) -> navigateToSelectGymLocation());

        // used to keep track of when user goes near their specified gym location
        geofencingClient = LocationServices.getGeofencingClient(this);

        addGymGeofence();

        db = AppDb.getInstance(getApplicationContext());

        new Thread(()->{
            Exercise i = db.exerciseDao().getById(1);
            if(i!=null) return;

           db.exerciseDao().insertExercise(new Exercise("Squat"));
            long benchId= db.exerciseDao().insertExercise(new Exercise("Panca piana"));
            db.exerciseDao().insertExercise(new Exercise("Deadlift"));
            long milId = db.exerciseDao().insertExercise(new Exercise("Military press"));
            db.exerciseDao().insertExercise(new Exercise("Stacco a gamba tesa"));
            long raiseId = db.exerciseDao().insertExercise(new Exercise("Alzate laterali"));
            db.exerciseDao().insertExercise(new Exercise("Curl con bilanciere"));
            long ropeId = db.exerciseDao().insertExercise(new Exercise("Rope pushdown"));
            db.exerciseDao().insertExercise(new Exercise("Pull-up"));
            db.exerciseDao().insertExercise(new Exercise("Push-up"));
            db.exerciseDao().insertExercise(new Exercise("Dips"));

           long pplId= db.trainingProgramDao().insertTrainingProgram(new TrainingProgram("PPL", ""));
           long dayId = db.trainingDayDao().insertTrainingDay(
                   new TrainingDay(pplId,
                           (short) LocalDate.now().getDayOfWeek().getValue()));

            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(benchId, dayId, (short)2, (short)12, (short)5));
            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(milId, dayId, (short)2, (short)12, (short)5));
            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(raiseId, dayId, (short)2, (short)12, (short)5));
            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(ropeId, dayId, (short)2, (short)12, (short)5));

        }).start();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.showOverflowMenu();
    }

    private void navigateToSelectGymLocation() {
        NavHostFragment navHostFragment =
                (NavHostFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        navHostFragment.getNavController().navigate(
                R.id.action_Main_to_GymMap
        );
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_gym_location) {
            navigateToSelectGymLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("MissingPermission")
    private void addGymGeofence() {
        LatLng gymLocation = getGymLocation();

        if(gymLocation == null) {
            return;
        }

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

        geofencingClient.addGeofences(
                new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                        .addGeofence(geofence)
                        .build(),
                getGymGeofencePendingIntent()
        ).addOnSuccessListener((aVoid) -> {
            System.out.println("geofence added");
            createNotificationChannel();
        }).addOnFailureListener((e) -> {
            System.out.println("FAILURE GEOFENCE");
            e.printStackTrace();
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION_CHANNEL";
            String description = "Gym geofence notifications channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private PendingIntent getGymGeofencePendingIntent() {
        if(geofencePendingIntent != null) {
            System.out.println("PENDING INTENT NOT NULL");
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GymGeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, 0 //PendingIntent.FLAG_UPDATE_CURRENT
        );
        System.out.println("CREATED PENDING INTENT");
        return geofencePendingIntent;
    }

    @Nullable
    public LatLng getGymLocation() {
        /**
         * Returns a LatLng object describing the location of user's gym saved in
         * preferences, or null if user didn't specify a gym location
         */

        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);

        double lat = prefs.getFloat(getString(R.string.gym_lat_key), 0);
        double lng = prefs.getFloat(getString(R.string.gym_lng_key), 0);

        System.out.println(lat + " " + lng);

        return lat != 0 && lng != 0 ? new LatLng(lat, lng) : null;
    }

}