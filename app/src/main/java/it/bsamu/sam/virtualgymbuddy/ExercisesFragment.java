package it.bsamu.sam.virtualgymbuddy;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import adapter.ExerciseAdapter;
import dialog.ExerciseCreationDialog;
import it.bsamu.sam.virtualgymbuddy.databinding.ExercisesFragmentBinding;
import relational.AppDb;
import relational.entities.Exercise;

public class ExercisesFragment extends Fragment implements View.OnClickListener, ExerciseCreationDialog.ExerciseCreationDialogListener {
    private ExercisesFragmentBinding binding;

    private ListView listView;
    private Cursor exercisesCursor;
    private ExerciseAdapter adapter;

    private AppDb db;

    private FloatingActionButton fab;
    private ExerciseCreationDialog exerciseCreationDialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDb.getInstance(getContext());



        // fetch all exercises
        new AsyncTask<Void,Void,Void>(){

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                exercisesCursor = db.exerciseDao().getAll(); /*new ArrayList<Exercise>();*/
                adapter = new ExerciseAdapter(
                        getContext(),
                        exercisesCursor
                );
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
                // TODO investigate: it's null when using an image
                //dialog.getDialog().cancel();
                return added;
            }

            @Override
            protected void onPostExecute(Exercise added) {
                super.onPostExecute(added);
                //exercises.add(0, added);
                adapter.notifyDataSetChanged();
                // TODO make toast notification
            }
        }.execute();
        System.out.println("received!");
    }

}
