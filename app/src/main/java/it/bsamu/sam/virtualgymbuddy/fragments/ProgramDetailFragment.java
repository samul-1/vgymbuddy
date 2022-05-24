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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import adapter.TrainingDayAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.ProgramDetailFragmentBinding;
import relational.entities.TrainingDay;
import relational.entities.TrainingProgram;

public class ProgramDetailFragment extends AbstractItemDetailFragment<TrainingDayAdapter> implements TrainingDayAdapter.TrainingDayViewHolderListener {
    private TrainingProgram program;
    private TextView programName;
    private TextView programDesc;
    private Button addDayBtn;

    List<String> weekDays;

    private ArrayAdapter<String> dropdownAdapter;
    Spinner dayOfWeekDropdown;
    ArrayList<String> daysOfWeek;
    List<TrainingDay> trainingDays = new LinkedList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superview = super.onCreateView(inflater, container, savedInstanceState);

        programName = superview.findViewById(R.id.program_detail_title);
        programDesc = superview.findViewById(R.id.program_detail_description);
        addDayBtn = superview.findViewById(R.id.add_training_day_btn);
        dayOfWeekDropdown = superview.findViewById(R.id.day_of_week_selection);

        weekDays = Arrays.asList(
                getResources().getStringArray(R.array.days_of_week)
        );

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
        /**
         * Filters out days for which a TrainingDay exists to prevent the user from
         * adding more than one TrainingDay for any given day of the week
         */

        List<Short> usedDays = trainingDays
                .stream()
                .map(t->t.dayOfWeek)
                .collect(Collectors.toList());

        // filter out days for which a training day exists
        List<String> usableDays =
                IntStream.range(1, weekDays.size()+1)
                .filter(i-> !usedDays.contains((short)i))
                .mapToObj(i->weekDays.get(i-1))
                .collect(Collectors.toList());

        if(daysOfWeek == null) {
            daysOfWeek = new ArrayList<>(
                usableDays
            );
        } else {
            daysOfWeek.clear();
            daysOfWeek.addAll(usableDays);
            dropdownAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void paintItemData() {
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
    protected TrainingDayAdapter instantiateAdapter() {
        return new TrainingDayAdapter(this, getContext(), trainingDays);
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.program_detail_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.program_detail_fragment, container,false);
    }

    private void insertTrainingDay() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                short selectedDayIdx = (short)(weekDays.indexOf(dayOfWeekDropdown.getSelectedItem())+1);
                db.trainingDayDao().insertTrainingDay(new TrainingDay(itemId, selectedDayIdx));
                trainingDays.clear();
                trainingDays.addAll(db.trainingDayDao().getForProgram(itemId));
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.notifyDataSetChanged();
                // update list of usable days (i.e. days for which a training day doesn't exist)
                setUsableDaysOfWeek();
            }
        }.execute();
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                trainingDays.clear();
                trainingDays.addAll(db.trainingDayDao().getForProgram(itemId));
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.notifyDataSetChanged();
                setUsableDaysOfWeek();
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
