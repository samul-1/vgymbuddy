package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.bsamu.sam.virtualgymbuddy.fragments.CurrentProgramFragment;
import it.bsamu.sam.virtualgymbuddy.fragments.ExercisesFragment;
import it.bsamu.sam.virtualgymbuddy.fragments.ProgramsFragment;

public class NavigationAdapter extends FragmentStateAdapter {


    public NavigationAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        System.out.println("creating adapter");
    }

    public NavigationAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public NavigationAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        System.out.println("calling createFragment with position " + position);
        switch (position) {
            case 0:
                return new ProgramsFragment();
            case 1:
                return new ExercisesFragment();
            case 2:
                return new CurrentProgramFragment();

        }
        throw new AssertionError();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
