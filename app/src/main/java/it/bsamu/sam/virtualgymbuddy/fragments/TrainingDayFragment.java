package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import adapter.TrainingDayExerciseAdapter;
import dialog.ExerciseSelectionDialog;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.TrainingDay;
import it.bsamu.sam.virtualgymbuddy.databinding.TrainingDayDetailBinding;
import relational.entities.TrainingDayExercise;

public class TrainingDayFragment extends AbstractItemDetailFragment<TrainingDayExerciseAdapter> implements ExerciseSelectionDialog.ExerciseSelectionDialogListener {
    private TrainingDay trainingDay;
    TextView title, selectedExerciseName;
    Button addExerciseBtn, saveExerciseBtn;
    ViewGroup addExerciseControls;
    private TrainingDayDetailBinding binding;
    long chosenExerciseId;
    String chosenExerciseName;
    EditText setsInput, repsInput, restInput;
    ExerciseSelectionDialog dialog;

    private final String BUNDLE_CHOSEN_EXERCISE_ID = "eid";
    private final String BUNDLE_CHOSEN_EXERCISE_NAME = "ename";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_CHOSEN_EXERCISE_ID, chosenExerciseId);
        outState.putString(BUNDLE_CHOSEN_EXERCISE_NAME, chosenExerciseName);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            long savedExerciseId = savedInstanceState.getLong(BUNDLE_CHOSEN_EXERCISE_ID, 0L);
            String savedExerciseName =  savedInstanceState.getString(BUNDLE_CHOSEN_EXERCISE_NAME, "");
            if(savedExerciseId != 0L) {
                setChosenExercise(savedExerciseId, savedExerciseName);
            }
        }
    }

    @Override
    protected TrainingDayExerciseAdapter instantiateAdapter() {
        return new TrainingDayExerciseAdapter();
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.training_day_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.training_day_detail, container,false);
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = db.trainingDayExerciseDao().getExercisesForTrainingDay(itemId);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superview = super.onCreateView(inflater, container, savedInstanceState);
        binding = TrainingDayDetailBinding.inflate(inflater, container, false);

        title = superview.findViewById(R.id.training_day_detail_position_title);
        selectedExerciseName = superview.findViewById(R.id.selected_exercise);
        addExerciseBtn = superview.findViewById(R.id.add_training_day_exercise_btn);
        saveExerciseBtn = superview.findViewById(R.id.save_training_day_exercise_btn);
        setsInput = superview.findViewById(R.id.training_day_exercise_input_sets);
        repsInput = superview.findViewById(R.id.training_day_exercise_input_reps);
        restInput = superview.findViewById(R.id.training_day_exercise_input_rest);
        addExerciseControls = superview.findViewById(R.id.training_day_exercise_controls);

        addExerciseBtn.setOnClickListener((__)->chooseExercise());
        saveExerciseBtn.setOnClickListener((__)->insertExercise());

       checkRequiredFields();

       TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        };

       setsInput.addTextChangedListener(tw);
       repsInput.addTextChangedListener(tw);
       restInput.addTextChangedListener(tw);

        return superview;
    }

    private void checkRequiredFields() {
        saveExerciseBtn.setEnabled(
                setsInput.getText().toString().length() > 0
                && repsInput.getText().toString().length() > 0
                && restInput.getText().toString().length() > 0
        );
    }

    private void chooseExercise() {
        dialog = new ExerciseSelectionDialog();
        dialog.show(
               getChildFragmentManager(), "exercise-selection-dialog"
        );
    }

    private void insertExercise() {
        short sets = Short.valueOf(setsInput.getText().toString());
        short reps = Short.valueOf(repsInput.getText().toString());
        short rest = Short.valueOf(restInput.getText().toString());

        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                db.trainingDayExerciseDao()
                        .insertTrainingDayExercise(
                                new TrainingDayExercise(
                                        chosenExerciseId,
                                        trainingDay.id,
                                        sets,
                                        reps,
                                        rest
                                )
                        );
                cursor = db.trainingDayExerciseDao().getExercisesForTrainingDay(itemId);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.swapCursor(cursor);
                adapter.notifyDataSetChanged();
                setChosenExercise(0L, "");
                setsInput.setText("");
                repsInput.setText("");
                restInput.setText("");
            }
        }.execute();
    }

    @Override
    protected void paintItemData() {
        title.setText(String.valueOf(
                getContext()
                        .getResources()
                        .getStringArray(R.array.days_of_week)[trainingDay.dayOfWeek-1]
                )
        );
    }

    @Override
    protected void asyncFetchItem() {
        new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                trainingDay = db.trainingDayDao().getById(itemId);
                return null;
            }
        }.execute();
    }

    @Override
    public void onExerciseSelection(long exerciseId, String exerciseName) {
        setChosenExercise(exerciseId, exerciseName);
    }

    private void setChosenExercise(long exerciseId, String exerciseName) {
        chosenExerciseId = exerciseId;
        chosenExerciseName = exerciseName;
        selectedExerciseName.setText(exerciseName);
        addExerciseBtn.setVisibility(exerciseId != 0L ? View.GONE : View.VISIBLE);
        addExerciseControls.setVisibility(exerciseId != 0L ? View.VISIBLE : View.GONE);
    }
}
