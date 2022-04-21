package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import adapter.TrainingDayExerciseAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.TrainingDay;

public class TrainingDayFragment extends AbstractItemDetailFragment<TrainingDayExerciseAdapter> {
    private TrainingDay trainingDay;

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
    protected void paintItemData() {

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
