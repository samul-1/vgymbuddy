package it.bsamu.sam.virtualgymbuddy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.TestFragmentBinding;
import it.bsamu.sam.virtualgymbuddy.viewmodel.TestViewModel;

public class TestFragment extends Fragment {
    TestViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.test_fragment, container, false);
        TestFragmentBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.test_fragment, container, false
        );
        model = ViewModelProviders.of(requireActivity()).get(TestViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(model);
        return binding.getRoot();
        //return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // model = ViewModelProviders.of(requireActivity()).get(TestViewModel.class);
        getActivity().findViewById(R.id.test_btn).setOnClickListener((v)-> increaseTestId());
    }

    public void increaseTestId() {
        model.setTestId(model.getTestId().getValue()+1);
    }
}
