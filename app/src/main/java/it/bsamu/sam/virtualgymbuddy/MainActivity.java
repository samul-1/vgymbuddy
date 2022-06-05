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

import androidx.appcompat.view.menu.MenuBuilder;
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
import util.GeofenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;

    private AppDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!GeofenceManager.hasGeofenceBeenAdded(this)) {
            GeofenceManager.addGymGeofence(this);
        }

        db = AppDb.getInstance(getApplicationContext());

        loadInitialData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void navigateToSelectGymLocation() {
        NavHostFragment navHostFragment =
                (NavHostFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        navHostFragment.getNavController().navigate(
                R.id.action_Main_to_GymMap
        );
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show options' icons
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        // Inflate the menu; this adds items to the action bar if it is present
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

    private void loadInitialData() {
        /**
         * Loads some initial data to the app db
         */
        new Thread(() -> {
            Exercise i = db.exerciseDao().getById(1);
            if(i != null) {
                // not the first time the app is launched; don't load any data
                return;
            }

            db.exerciseDao().insertExercise(new Exercise("Squat"));
            long benchId = db.exerciseDao().insertExercise(new Exercise("Panca piana"));
            db.exerciseDao().insertExercise(new Exercise("Deadlift"));
            long milId = db.exerciseDao().insertExercise(new Exercise("Military press"));
            db.exerciseDao().insertExercise(new Exercise("Stacco a gamba tesa"));
            long raiseId = db.exerciseDao().insertExercise(new Exercise("Alzate laterali"));
            db.exerciseDao().insertExercise(new Exercise("Curl con bilanciere"));
            long ropeId = db.exerciseDao().insertExercise(new Exercise("Rope pushdown"));
            db.exerciseDao().insertExercise(new Exercise("Pull-up"));
            db.exerciseDao().insertExercise(new Exercise("Push-up"));
            db.exerciseDao().insertExercise(new Exercise("Dips"));

            long pplId = db.trainingProgramDao().insertTrainingProgram(new TrainingProgram("PPL", "Routine di allenamento push-pull-legs"));
            long dayId = db.trainingDayDao().insertTrainingDay(
                    new TrainingDay(pplId,
                            (short) LocalDate.now().getDayOfWeek().getValue()));

            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(benchId, dayId, (short)2, (short)12, (short)5));
            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(milId, dayId, (short)2, (short)12, (short)5));
            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(raiseId, dayId, (short)2, (short)12, (short)5));
            db.trainingDayExerciseDao().insertTrainingDayExercise(new TrainingDayExercise(ropeId, dayId, (short)2, (short)12, (short)5));
        }).start();
    }
}