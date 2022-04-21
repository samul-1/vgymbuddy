package adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;
public class TrainingDayExerciseAdapter  extends AbstractCursorAdapter<TrainingDayExerciseAdapter.TrainingDayExerciseViewHolder> {
    public TrainingDayExerciseAdapter() {
        super(null);
        System.out.println("instantiating adapterr");
    }

    @Override
    public void onBindViewHolder(TrainingDayExerciseAdapter.TrainingDayExerciseViewHolder holder, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        short sets = cursor.getShort(cursor.getColumnIndexOrThrow("setsPrescribed"));
        short reps = cursor.getShort(cursor.getColumnIndexOrThrow("repsPrescribed"));
        //System.out.println("NAME " + name);
        holder.trainingDayExerciseExerciseName.setText(String.valueOf(name));
        holder.trainingDayExerciseExerciseSets.setText(String.valueOf(String.valueOf(sets)));
        holder.trainingDayExerciseExerciseReps.setText(String.valueOf(String.valueOf(reps)));
    }

    @NonNull
    @Override
    public TrainingDayExerciseAdapter.TrainingDayExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("on creating view holder");
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.training_day_exercise_item, parent, false);
        return new TrainingDayExerciseAdapter.TrainingDayExerciseViewHolder(formNameView);
    }


    class TrainingDayExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView trainingDayExerciseExerciseName;
        TextView trainingDayExerciseExerciseReps;
        TextView trainingDayExerciseExerciseSets;
        TrainingDayExerciseViewHolder(View itemView) {
            super(itemView);
            trainingDayExerciseExerciseName = itemView.findViewById(R.id.training_day_exercise_name);
            trainingDayExerciseExerciseSets = itemView.findViewById(R.id.training_day_exercise_sets);
            trainingDayExerciseExerciseReps = itemView.findViewById(R.id.training_day_exercise_reps);
        }
    }
}
