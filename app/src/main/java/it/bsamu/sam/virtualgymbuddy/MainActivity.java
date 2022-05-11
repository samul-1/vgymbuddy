package it.bsamu.sam.virtualgymbuddy;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import adapter.NavigationAdapter;
import it.bsamu.sam.virtualgymbuddy.databinding.ActivityMainBinding;
import relational.AppDb;
import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingProgram;

import android.view.Menu;
import android.view.MenuItem;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;

    private AppDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}