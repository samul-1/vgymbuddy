package it.bsamu.sam.virtualgymbuddy;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import adapter.ExerciseAdapter;
import dialog.ExerciseCreationDialog;
import it.bsamu.sam.virtualgymbuddy.databinding.ExercisesFragmentBinding;
import relational.AppDb;
import relational.entities.Exercise;

public class ExercisesFragment extends Fragment implements View.OnClickListener, ExerciseCreationDialog.ExerciseCreationDialogListener {
    private ExercisesFragmentBinding binding;

    private ListView listView;
    private ArrayList<Exercise> exercises;
    private ExerciseAdapter adapter;

    private AppDb db;

    private FloatingActionButton fab;
    private ExerciseCreationDialog exerciseCreationDialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDb.getInstance(getContext());

        exercises = new ArrayList<Exercise>();
        adapter = new ExerciseAdapter(
                getContext(),
                R.layout.exercise_card,
                //android.R.layout.simple_spinner_item,
                exercises
        );

        // fetch all exercises
        new AsyncTask<Void,Void,Void>(){

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                exercises.addAll(db.exerciseDao().getAll());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = ExercisesFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView)getView().findViewById(R.id.exercise_listview);
        listView.setAdapter(adapter);

        fab = (FloatingActionButton) getView().findViewById(R.id.exercises_fragment_fab);
        fab.setOnClickListener(this);
//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == fab) {
                exerciseCreationDialog = exerciseCreationDialog==null ? new ExerciseCreationDialog() : exerciseCreationDialog;
                exerciseCreationDialog.show(getActivity().getSupportFragmentManager(), "exercise-creation-dialog");
                return;
        }
        System.out.println("OPENED VIEW" + view.toString());
        throw new AssertionError();
    }

    @Override
    public void onCreateExercise(DialogFragment dialog, String exerciseName) {
        new AsyncTask<String,Void,Void>(){
            @Override
            protected Void doInBackground(String... exerciseNames) {
                System.out.println("received" + exerciseNames[0]);
                db.exerciseDao().insertExercise(new Exercise(exerciseNames[0]));
                dialog.getDialog().cancel();
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.notifyDataSetChanged();
            }
        }.execute(new String[]{exerciseName});
        System.out.println("received!");
    }

}
