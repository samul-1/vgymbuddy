package it.bsamu.sam.virtualgymbuddy.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabBar);

        // set up view pager and attach mediator to tab layout
        viewPager = getActivity().findViewById(R.id.pager);
        navigationAdapter = new NavigationAdapter(this);
        viewPager.setAdapter(navigationAdapter);
        // set tab items' texts
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(
                       navigationAdapter.tabTexts[position]
                )
        ).attach();
    }
}
