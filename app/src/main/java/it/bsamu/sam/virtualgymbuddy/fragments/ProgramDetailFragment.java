package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Arrays;

import adapter.TrainingDayAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.ProgramDetailFragmentBinding;
import relational.entities.TrainingDay;
import relational.entities.TrainingProgram;

public class ProgramDetailFragment extends AbstractItemDetailFragment<TrainingDayAdapter> implements TrainingDayAdapter.TrainingDayViewHolderListener {
    private TrainingProgram program;
    private ProgramDetailFragmentBinding binding;
    private TextView programName;
    private TextView programDesc;
    private Button addDayBtn;

    private ArrayAdapter<String> dropdownAdapter;
    Spinner dayOfWeekDropdown;
    ArrayList<String> daysOfWeek;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superview = super.onCreateView(inflater, container, savedInstanceState);
        binding = ProgramDetailFragmentBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        programName = superview.findViewById(R.id.program_detail_title);
        programDesc = superview.findViewById(R.id.program_detail_description);
        addDayBtn = superview.findViewById(R.id.add_training_day_btn);
        dayOfWeekDropdown = superview.findViewById(R.id.day_of_week_selection);

        setUsableDaysOfWeek();
        dropdownAdapter = new ArrayAdapter<>(
                getContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                daysOfWeek
        );
        dayOfWeekDropdown.setAdapter(dropdownAdapter);

        addDayBtn.setOnClickListener((__)->insertTrainingDay());

        return superview;
    }

    @NonNull
    private void setUsableDaysOfWeek() {
        if(daysOfWeek == null) {
            daysOfWeek = new ArrayList<>(
                    Arrays.asList(
                            getResources().getStringArray(R.array.days_of_week)
                    )
            );
        }
        // TODO remove days that have already been used
    }


    @Override
    protected void paintItemData() {
        System.out.println("painting");
        if(program != null) {
            programName.setText(program.name);
            programDesc.setText(program.description);
        } else {
            throw new AssertionError("Program is null at the time of onCreateView");
        }
    }

    @Override
    protected void asyncFetchItem() {
        new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                program = db.trainingProgramDao().getById(itemId);
                return null;
            }
        }.execute();
    }

    @Override
    protected TrainingDayAdapter getAdapter() {
        return new TrainingDayAdapter(this, getContext());
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.program_detail_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        System.out.println("getting program detail inflate");
        return inflater.inflate(R.layout.program_detail_fragment, container,false);
    }

    private void insertTrainingDay() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                short selectedDayIdx = (short)(dayOfWeekDropdown.getSelectedItemPosition()+1);
                // to allow max one training day per day of week, remove selected day from dropdown

                db.trainingDayDao().insertTrainingDay(new TrainingDay(itemId, selectedDayIdx));
                cursor = db.trainingDayDao().getForProgram(itemId);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.swapCursor(cursor);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = db.trainingDayDao().getForProgram(itemId);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.swapCursor(cursor);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void navigateToTrainingDayDetails(long dayId) {
        Bundle args = new Bundle();
        args.putLong(AbstractItemDetailFragment.ITEM_ID_ARG, dayId);


        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        navHostFragment.getNavController().navigate(
                R.id.action_ProgramDetail_to_TrainingDay, args
        );
    }
}
