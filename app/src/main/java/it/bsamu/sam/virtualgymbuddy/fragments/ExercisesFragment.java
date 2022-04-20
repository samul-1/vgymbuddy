package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import adapter.ExerciseAdapter;
import dialog.ExerciseCreationDialog;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.ExercisesFragmentBinding;
import relational.AppDb;
import relational.entities.Exercise;

public class ExercisesFragment extends AbstractCursorRecyclerViewFragment<ExerciseAdapter> implements View.OnClickListener, ExerciseCreationDialog.ExerciseCreationDialogListener {
    private ExercisesFragmentBinding binding;

    private FloatingActionButton fab;
    private ExerciseCreationDialog exerciseCreationDialog = null;


    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = db.exerciseDao().getAll();
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) getView().findViewById(R.id.exercises_fragment_fab);
        fab.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == fab) {
                exerciseCreationDialog = exerciseCreationDialog == null ?
                        new ExerciseCreationDialog() : exerciseCreationDialog;
                exerciseCreationDialog.show(
                        getActivity().getSupportFragmentManager(), "exercise-creation-dialog"
                );
                return;
        }
        throw new AssertionError();
    }

    @Override
    public void onCreateExercise(DialogFragment dialog, String exerciseName, Uri pickedImg) {
        new AsyncTask<Void,Void,Exercise>(){
            @Override
            protected Exercise doInBackground(Void... voids) {
                System.out.println("received" + exerciseName);
                // create new exercise
                Exercise added = new Exercise(exerciseName);
                long id = db.exerciseDao().insertExercise(added);

                // save picked img
                if(pickedImg != null) {
                    try(FileOutputStream fos = getContext().openFileOutput(String.valueOf(id)+".jpg", Context.MODE_PRIVATE)) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImg);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        //fos.write(bitmap.to);
                   } catch (FileNotFoundException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                }
                return added;
            }

            @Override
            protected void onPostExecute(Exercise added) {
                super.onPostExecute(added);
                // TODO fix glitch: new card contains image of a card that was in view
                asyncFetchMainEntity();
                Toast.makeText(getContext(), R.string.toast_exercise_created, Toast.LENGTH_SHORT).show();
            }
        }.execute();
        System.out.println("received!");
    }


    @Override
    protected ExerciseAdapter getAdapter() {
        return new ExerciseAdapter();
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.exercise_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.exercises_fragment, container,false);
    }
}
