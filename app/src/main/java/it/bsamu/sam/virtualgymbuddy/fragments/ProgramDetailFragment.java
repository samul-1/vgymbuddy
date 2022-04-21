package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;



import adapter.TrainingDayAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.ProgramDetailBinding;
import relational.entities.TrainingProgram;

public class ProgramDetailFragment extends AbstractCursorRecyclerViewFragment<TrainingDayAdapter> {
    public static final String PROGRAM_ITEM_ID = "program_id";
    private long programId;
    private TrainingProgram program;
    private ProgramDetailBinding binding;
    private TextView programName;
    private TextView programDesc;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View superview = super.onCreateView(inflater, container, savedInstanceState);
        binding = ProgramDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        programName = superview.findViewById(R.id.program_detail_title);
        programDesc = superview.findViewById(R.id.program_detail_description);

        paintProgramData();
        System.out.println("inside detail view");
        return superview;
    }

    private void paintProgramData() {
        System.out.println("PAINTING NAME "+ program.name);
        if(program != null) {
            programName.setText(program.name);
            programDesc.setText(program.description);
        } else {
            throw new AssertionError("Program is null at the time of onCreateView");
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
        System.out.println("getting program detail inflate");
        return inflater.inflate(R.layout.program_detail, container,false);
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = db.trainingDayDao().getAll();
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
}
