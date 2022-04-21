package it.bsamu.sam.virtualgymbuddy;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import adapter.NavigationAdapter;
import it.bsamu.sam.virtualgymbuddy.databinding.ActivityMainBinding;
import relational.AppDb;
import relational.entities.TrainingDay;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private ViewPager2 viewPager;
    private NavigationAdapter navigationAdapter;

    private AppDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDb.getInstance(getApplicationContext());

        new Thread(()->{
            /*TrainingDay d = new TrainingDay();
            d.trainingProgramId = 1;
            d.position = 2;
            db.trainingDayDao().insertTrainingDay(d);*/
            /*Exercise ex1 = new Exercise();
            Exercise ex2 = new Exercise();
            Exercise ex3 = new Exercise();
            Exercise ex4 = new Exercise();
            Exercise ex5 = new Exercise();
            Exercise ex6 = new Exercise();
            Exercise ex7 = new Exercise();
            Exercise ex8 = new Exercise();
            Exercise ex9 = new Exercise();
            Exercise ex10 = new Exercise();
            Exercise ex11 = new Exercise();
            Exercise ex12 = new Exercise();
            ex1.name = "Squat";
            ex2.name = "Military press";
            ex3.name = "Deadlift";
            ex4.name = "Panca piana";
            ex5.name = "Panca inclinata";
            ex6.name = "Rematore con bilanciere";
            ex7.name = "Lat machine";
            ex8.name = "Alzate laterali";
            ex9.name = "Pullup";
            ex10.name = "Pushup";
            ex11.name = "Dip";
            ex12.name = "Stacco a gamba tesa";
            db.exerciseDao().insertExercise(ex1);
            db.exerciseDao().insertExercise(ex2);
            db.exerciseDao().insertExercise(ex3);
            db.exerciseDao().insertExercise(ex4);
            db.exerciseDao().insertExercise(ex5);
            db.exerciseDao().insertExercise(ex6);
            db.exerciseDao().insertExercise(ex7);
            db.exerciseDao().insertExercise(ex8);
            db.exerciseDao().insertExercise(ex9);
            db.exerciseDao().insertExercise(ex10);
            db.exerciseDao().insertExercise(ex11);
            db.exerciseDao().insertExercise(ex12);*/
        }).start();
        System.out.println("db built");
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