package dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adapter.ExerciseAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.AppDb;

public class ExerciseSelectionDialog extends DialogFragment implements ExerciseAdapter.ExerciseViewHolderListener {
    /**
     * Handles exercise selection from the dialog
     */
    public interface ExerciseSelectionDialogListener {
        void onExerciseSelection(long exerciseId, String exerciseName);
    }

    RecyclerView recyclerView;
    Cursor cursor;
    ExerciseAdapter adapter;
    AppDb db = AppDb.getInstance(getContext());

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.exercise_selection_dialog, null);
        recyclerView = view.findViewById(R.id.exercise_selection_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = getAdapter();
        recyclerView.setAdapter(adapter);

         builder
                .setView(view)
                .setTitle(R.string.exercise_selection_title)
                .setNeutralButton(
                        R.string.cancel,
                        (dialog, id) -> ExerciseSelectionDialog.this.getDialog().cancel()
                );


         asyncFetchExercises();
         return builder.create();
    }



    private void asyncFetchExercises() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                // TODO exclude exercises already in the training day
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

    private ExerciseAdapter getAdapter() {
        return new ExerciseAdapter(this);
    }

    @Override
    public void onExerciseClick(long exerciseId, String exerciseName) {
        // call parent's fragment method to signal an exercise has been selected
        ((ExerciseSelectionDialogListener)
                getParentFragment()
        ).onExerciseSelection(exerciseId, exerciseName);
        dismiss();
    }
}
