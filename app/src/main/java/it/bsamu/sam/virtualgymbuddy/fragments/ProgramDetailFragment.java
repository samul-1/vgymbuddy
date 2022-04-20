package it.bsamu.sam.virtualgymbuddy.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import adapter.TrainingDayAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.Exercise;
import relational.entities.TrainingProgram;

public class ProgramDetailFragment extends AbstractCursorRecyclerViewFragment<TrainingDayAdapter> {
    public static final String PROGRAM_ITEM_ID = "program_id";
    private long programId;
    private TrainingProgram program;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(PROGRAM_ITEM_ID)) {
            // get data for selected program
           programId = getArguments().getLong(PROGRAM_ITEM_ID);
           asyncFetchProgram();
        } else {
            throw new AssertionError("no program id");
        }
    }

    private void asyncFetchProgram() {
        new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                System.out.println("fetching program " + programId);
                program = db.trainingProgramDao().getById(programId);
                return null;
            }
        }.execute();
    }

    @Override
    protected TrainingDayAdapter getAdapter() {
        return new TrainingDayAdapter();
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.program_detail_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.program_detail, container,false);
    }

    @Override
    protected void asyncFetchMainEntity() {

    }
}
