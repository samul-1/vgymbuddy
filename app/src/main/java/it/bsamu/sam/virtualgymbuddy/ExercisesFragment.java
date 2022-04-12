package it.bsamu.sam.virtualgymbuddy;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import java.util.ArrayList;

import it.bsamu.sam.virtualgymbuddy.databinding.ExercisesFragmentBinding;
import relational.AppDb;
import relational.entities.Exercise;

public class ExercisesFragment extends Fragment {
    private ExercisesFragmentBinding binding;

    private ListView listView;
    private ArrayList<Exercise> exercises;
    private ArrayAdapter<Exercise> adapter;

    private AppDb db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(
                getContext(),
                AppDb.class,
                "db"
        ).build();

        exercises = new ArrayList<Exercise>();
        adapter = new ArrayAdapter<Exercise>(
                getContext(),
                android.R.layout.simple_spinner_item,
                exercises
        );
        new AsyncTask<Void,Void,Void>(){

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                exercises.addAll(db.exerciseDao().getAll());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                adapter.notifyDataSetChanged();
            }
        }.execute();

/*        new Thread(()->{
            exercises.addAll(db.exerciseDao().getAll());
            adapter.notifyDataSetChanged();
        }).start();
*/
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
}
