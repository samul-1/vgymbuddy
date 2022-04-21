package adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;
public class TrainingDayAdapter  extends AbstractCursorAdapter<TrainingDayAdapter.TrainingDayViewHolder> {
    TrainingDayViewHolderListener listener;

    public TrainingDayAdapter(TrainingDayViewHolderListener listener) {
        super(null);
        this.listener = listener;
        System.out.println("instantiating adapter");
    }



    public interface TrainingDayViewHolderListener {
        void navigateToTrainingDayDetails(long dayId);
    }

    @Override
    public void onBindViewHolder(TrainingDayAdapter.TrainingDayViewHolder holder, Cursor cursor) {
        short position = cursor.getShort(cursor.getColumnIndexOrThrow("position"));
        long dayId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        holder.trainingDayPosition.setText(String.valueOf(position));
        holder.itemView.setOnClickListener((__)->listener.navigateToTrainingDayDetails(dayId));
    }

    @NonNull
    @Override
    public TrainingDayAdapter.TrainingDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.training_day_item, parent, false);
        return new TrainingDayAdapter.TrainingDayViewHolder(formNameView);
    }

    class TrainingDayViewHolder extends RecyclerView.ViewHolder {
        TextView trainingDayPosition;
        long dayId;
        TrainingDayViewHolder(View itemView) {
            super(itemView);
            trainingDayPosition = itemView.findViewById(R.id.training_day_position);
        }
    }

    /*class TrainingDayViewHolder extends RecyclerView.ViewHolder {
        TextView trainingDayExerciseName;
        TextView trainingDayExerciseReps;
        TextView trainingDayExerciseSets;
        TrainingDayViewHolder(View itemView) {
            super(itemView);
            trainingDayExerciseName = itemView.findViewById(R.id.training_day_exercise_name);
            trainingDayExerciseSets = itemView.findViewById(R.id.training_day_exercise_sets);
            trainingDayExerciseReps = itemView.findViewById(R.id.training_day_exercise_reps);
        }
    }*/
}
