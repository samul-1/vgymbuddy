package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import adapter.TrainingDayExerciseAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.TrainingDay;
import it.bsamu.sam.virtualgymbuddy.databinding.TrainingDayDetailBinding;
import relational.entities.TrainingDayExercise;

public class TrainingDayFragment extends AbstractItemDetailFragment<TrainingDayExerciseAdapter> {
    private TrainingDay trainingDay;
    TextView title;
    Button addExerciseBtn;
    private TrainingDayDetailBinding binding;
    long chosenExerciseId;
    EditText setsInput, repsInput;

    @Override
    protected TrainingDayExerciseAdapter getAdapter() {
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
        addExerciseBtn = superview.findViewById(R.id.add_training_day_exercise_btn);
        setsInput = superview.findViewById(R.id.training_day_exercise_input_sets);
        repsInput = superview.findViewById(R.id.training_day_exercise_input_reps);

        addExerciseBtn.setOnClickListener((__)->insertExercise());
        return superview;
    }

    private void insertExercise() {
        short sets = Short.valueOf(setsInput.getText().toString());
        short reps = Short.valueOf(repsInput.getText().toString());
        chosenExerciseId = 4; // TODO implement exercise choice
        System.out.println("E " + chosenExerciseId + " R " + reps + " S " + sets);

        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                db.trainingDayExerciseDao()
                        .insertTrainingDayExercise(
                                new TrainingDayExercise(
                                        chosenExerciseId, trainingDay.id, sets, reps
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
            }
        }.execute();
    }

    @Override
    protected void paintItemData() {
        title.setText(String.valueOf(
                getContext()
                        .getResources()
                        .getStringArray(R.array.days_of_week)[trainingDay.dayOfWeek]
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
}
