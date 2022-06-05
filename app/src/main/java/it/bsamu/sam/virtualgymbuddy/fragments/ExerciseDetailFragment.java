package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import adapter.ExerciseSetAdapter;
import adapter.TrainingSessionSetAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.fragments.AbstractItemDetailFragment;
import relational.entities.Exercise;

public class ExerciseDetailFragment extends AbstractItemDetailFragment<ExerciseSetAdapter> {
    private Exercise exercise;
    TextView title;
    LinearLayout emptyState;

    @Override
    protected ExerciseSetAdapter instantiateAdapter() {
        return new ExerciseSetAdapter();
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.exercise_detail_recyclerview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superview = super.onCreateView(inflater, container, savedInstanceState);
        title = superview.findViewById(R.id.exercise_detail_title);
        emptyState = superview.findViewById(R.id.exercise_detail_empty_state);
        return superview;
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
                cursor = db.trainingSessionSetDao().getForExerciseWithVideo(itemId);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.swapCursor(cursor);
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount() == 0) {
                    emptyState.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    protected void paintItemData() {
        title.setText(exercise.name);
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
