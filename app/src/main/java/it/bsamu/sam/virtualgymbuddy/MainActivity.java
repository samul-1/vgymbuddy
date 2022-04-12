package it.bsamu.sam.virtualgymbuddy;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import it.bsamu.sam.virtualgymbuddy.databinding.ActivityMainBinding;
import relational.AppDb;
import relational.entities.Exercise;

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

        TabLayout tabLayout = findViewById(R.id.tabBar);
        int[] tabTexts = {
                R.string.tab_programs,
                R.string.tab_exercises,
                R.string.tab_current_program
        };

        // set up view pager and attach mediator to tab layout
        this.viewPager = findViewById(R.id.pager);
        this.navigationAdapter = new NavigationAdapter(this);
        viewPager.setAdapter(this.navigationAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(
                        tabTexts[position]
                )
        ).attach();

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDb.class,
                "db"
        ).build();
        /*new Thread(()->{
            Exercise ex1 = new Exercise();
            Exercise ex2 = new Exercise();
            Exercise ex3 = new Exercise();
            ex1.name = "Squat";
            ex2.name = "Military press";
            ex3.name = "Deadlift";
            db.exerciseDao().insertExercise(ex1);
            db.exerciseDao().insertExercise(ex2);
            db.exerciseDao().insertExercise(ex3);
        }).start();
        System.out.println("db built");*/
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