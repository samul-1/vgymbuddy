package adapter;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;

public class ExerciseSetAdapter extends AbstractCursorAdapter<ExerciseSetAdapter.ExerciseSetViewHolder>{

    public ExerciseSetAdapter() {
        super(null);
    }

    @Override
    public void onBindViewHolder(ExerciseSetViewHolder holder, Cursor cursor) {

    }

    @NonNull
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    class ExerciseSetViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView repsView;
        TextView weightView;
        ExerciseSetViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.set_item_video);
            repsView = itemView.findViewById(R.id.set_item_reps);
            weightView = itemView.findViewById(R.id.set_item_weight);
        }
    }
}
