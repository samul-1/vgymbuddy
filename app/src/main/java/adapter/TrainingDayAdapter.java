package adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;
public class TrainingDayAdapter  extends AbstractCursorAdapter<TrainingDayAdapter.TrainingDayViewHolder> {
    public TrainingDayAdapter(Cursor c) {
        super(c);
    }
    public TrainingDayAdapter() {
        super(null);
        System.out.println("instantiating adapter");
    }

    @Override
    public void onBindViewHolder(TrainingDayAdapter.TrainingDayViewHolder holder, Cursor cursor) {
        // TODO implement
        holder.trainingDayExerciseSets.setText("1");
        holder.trainingDayExerciseReps.setText("12");
        holder.trainingDayExerciseName.setText("Squat");
    }

    @NonNull
    @Override
    public TrainingDayAdapter.TrainingDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.training_day, parent, false);
        return new TrainingDayAdapter.TrainingDayViewHolder(formNameView);
    }

    class TrainingDayViewHolder extends RecyclerView.ViewHolder {
        TextView trainingDayExerciseName;
        TextView trainingDayExerciseReps;
        TextView trainingDayExerciseSets;
        TrainingDayViewHolder(View itemView) {
            super(itemView);
            trainingDayExerciseName = itemView.findViewById(R.id.training_day_exercise_name);
            trainingDayExerciseSets = itemView.findViewById(R.id.training_day_exercise_sets);
            trainingDayExerciseReps = itemView.findViewById(R.id.training_day_exercise_reps);
        }
    }
}
