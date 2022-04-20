package adapter;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.TrainingDay;

public class TrainingDayAdapter  extends AbstractCursorAdapter<TrainingProgramAdapter.TrainingProgramViewHolder> {
    public TrainingDayAdapter(Cursor c) {
        super(c);
    }
    public TrainingDayAdapter() {
        super(null);
    }

    @Override
    public void onBindViewHolder(TrainingProgramAdapter.TrainingProgramViewHolder holder, Cursor cursor) {

    }

    @NonNull
    @Override
    public TrainingProgramAdapter.TrainingProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    class TrainingDayViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameView;
        ImageView exerciseImgView;
        TrainingDayViewHolder(View itemView) {
            super(itemView);
            exerciseNameView = itemView.findViewById(R.id.exercise_name);
            exerciseImgView = itemView.findViewById(R.id.exercise_img);
        }
    }
}
