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
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
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

public class ProgramsFragment extends AbstractCursorRecyclerViewFragment<TrainingProgramAdapter> implements View.OnClickListener, TrainingProgramCreationDialog.TrainingProgramCreationDialogListener, TrainingProgramAdapter.TrainingProgramViewHolderListener {

    private ProgramsFragmentBinding binding;

    private FloatingActionButton fab;
    private TrainingProgramCreationDialog programCreationDialog = null;



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) getView().findViewById(R.id.programs_fragment_fab);
        fab.setOnClickListener(this);

        System.out.println("created me" + this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    protected TrainingProgramAdapter getAdapter() {
        return new TrainingProgramAdapter(this);
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.program_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.programs_fragment, container,false);
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = db.trainingProgramDao().getAll();
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
    public void onClick(View view) {
        /*System.out.println("clicked " + view + " fab is " + fab);
        if(view == fab) {*/
            programCreationDialog = programCreationDialog==null ?
                    new TrainingProgramCreationDialog(this) : programCreationDialog;
            programCreationDialog.show(
                    getActivity().getSupportFragmentManager(), "program-creation-dialog"
            );
            return;
        //}
        //throw new AssertionError();
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
                asyncFetchMainEntity();
                Toast.makeText(getContext(), R.string.toast_program_created, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void navigateToProgramDetails(long programId) {
        Bundle args = new Bundle();
        args.putLong(AbstractItemDetailFragment.ITEM_ID_ARG, programId);


        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        navHostFragment.getNavController().navigate(
                R.id.action_Main_to_ProgramDetail, args
        );
    }
}