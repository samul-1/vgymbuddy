package it.bsamu.sam.virtualgymbuddy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import adapter.NavigationAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {
    private ViewPager2 viewPager;
    private NavigationAdapter navigationAdapter;

    private static final String BUNDLE_CURRENT_ITEM_KEY = "ci";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_CURRENT_ITEM_KEY, viewPager.getCurrentItem());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("on create main " + this);

        System.out.println("MAIN BUNDLE NULL");
        //getActivity().setContentView(R.layout.main_fragment);
        /*        TabLayout tabLayout = getActivity().findViewById(R.id.tabBar);
        int[] tabTexts = {
                R.string.tab_programs,
               R.string.tab_exercises,
                R.string.tab_current_program
        };

        // set up view pager and attach mediator to tab layout
        this.viewPager = getActivity().findViewById(R.id.pager);
        this.navigationAdapter = new NavigationAdapter(this);
        viewPager.setAdapter(this.navigationAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(
                        tabTexts[position]
                )
        ).attach();*/

        if(savedInstanceState == null) {

        } else {
            viewPager.setCurrentItem(savedInstanceState.getInt(BUNDLE_CURRENT_ITEM_KEY, 2));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabBar);
        int[] tabTexts = {
                R.string.tab_programs,
                R.string.tab_exercises,
                R.string.tab_current_program
        };

        // set up view pager and attach mediator to tab layout
        this.viewPager = getActivity().findViewById(R.id.pager);
        this.navigationAdapter = new NavigationAdapter(this);
        viewPager.setAdapter(this.navigationAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(
                        tabTexts[position]
                )
        ).attach();
    }
}
