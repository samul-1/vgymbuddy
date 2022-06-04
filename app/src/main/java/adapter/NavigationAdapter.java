package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.fragments.CurrentProgramFragment;
import it.bsamu.sam.virtualgymbuddy.fragments.ExercisesFragment;
import it.bsamu.sam.virtualgymbuddy.fragments.ProgramsFragment;
import it.bsamu.sam.virtualgymbuddy.fragments.StatsFragment;

public class NavigationAdapter extends FragmentStateAdapter {
    public NavigationAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public final int[] tabTexts = {
            R.string.tab_programs,
            R.string.tab_exercises,
            R.string.tab_current_program,
            R.string.tab_stats
    };

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProgramsFragment();
            case 1:
                return new ExercisesFragment();
            case 2:
                return new CurrentProgramFragment();
            case 3:
                return new StatsFragment();

        }
        // not supposed to happen - just to avoid the Java complaining
        throw new AssertionError();
    }

    @Override
    public int getItemCount() {
        return tabTexts.length;
    }
}
