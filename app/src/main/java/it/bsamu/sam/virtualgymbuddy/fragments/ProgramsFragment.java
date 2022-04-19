package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import adapter.ExerciseAdapter;
import adapter.TrainingProgramAdapter;
import dialog.ExerciseCreationDialog;
import dialog.TrainingProgramCreationDialog;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.ProgramsFragmentBinding;
import relational.AppDb;
import relational.entities.Exercise;
import relational.entities.TrainingProgram;

public class ProgramsFragment extends Fragment implements View.OnClickListener, TrainingProgramCreationDialog.TrainingProgramCreationDialogListener {

    private ProgramsFragmentBinding binding;
    private RecyclerView recyclerView;
    private Cursor programsCursor;
    private TrainingProgramAdapter adapter;

    private AppDb db;

    private FloatingActionButton fab;
    private TrainingProgramCreationDialog programCreationDialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDb.getInstance(getContext());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ProgramsFragmentBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.programs_fragment, container,false);

        // create recycler view and set its adapter
        adapter = new TrainingProgramAdapter();
        recyclerView = view.findViewById(R.id.program_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        fetchPrograms();
        return view;

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) getView().findViewById(R.id.programs_fragment_fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchPrograms() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                programsCursor = db.trainingProgramDao().getAll();
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.swapCursor(programsCursor);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        if(view == fab) {
            programCreationDialog = programCreationDialog==null ?
                    new TrainingProgramCreationDialog() : programCreationDialog;
            programCreationDialog.show(
                    getActivity().getSupportFragmentManager(), "program-creation-dialog"
            );
            return;
        }
        throw new AssertionError();
    }

    @Override
    public void onCreateProgram(DialogFragment dialog, String programName, String programDescription) {
        new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                TrainingProgram program = new TrainingProgram(programName, programDescription);
                db.trainingProgramDao().insertTrainingProgram(program);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                //fetchExercises();
                Toast.makeText(getContext(), R.string.toast_program_created, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}