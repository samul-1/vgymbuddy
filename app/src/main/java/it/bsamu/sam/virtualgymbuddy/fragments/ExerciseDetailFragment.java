package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import adapter.ExerciseSetAdapter;
import adapter.TrainingSessionSetAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.fragments.AbstractItemDetailFragment;
import relational.entities.Exercise;

public class ExerciseDetailFragment extends AbstractItemDetailFragment<ExerciseSetAdapter> {
    private Exercise exercise;
    @Override
    protected ExerciseSetAdapter getAdapter() {
        return new ExerciseSetAdapter();
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.exercise_detail_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.exercise_detail_fragment, container,false);
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = db.trainingSessionSetDao().getForExercise(itemId);
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

    }

    @Override
    protected void asyncFetchItem() {
        new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                exercise = db.exerciseDao().getById(itemId);
                return null;
            }
        }.execute();
    }
}
