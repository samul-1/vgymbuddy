package adapter;

import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import it.bsamu.sam.virtualgymbuddy.R;

public class ExerciseAdapter extends AbstractCursorAdapter<ExerciseAdapter.ExerciseViewHolder> {
    public ExerciseAdapter(ExerciseViewHolderListener listener) {
        super(null);
        this.listener = listener;
    }
    private ExerciseViewHolderListener listener;
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(formNameView, listener);
    }
    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, Cursor cursor) {
        // fetch exercise's data
        holder.exerciseName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        holder.exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));

        holder.exerciseNameView.setText(holder.exerciseName);

        // if the exercise has an image associated to it, set it as card preview
        if(imageUriString.length()>0) {
            Uri exerciseImgUri = Uri.parse(imageUriString);
            holder.exerciseImgView.setImageURI(exerciseImgUri);
        }

    }

    public interface ExerciseViewHolderListener {
        void onExerciseClick(long exerciseId, String exerciseName);
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView exerciseNameView;
        ImageView exerciseImgView;
        long exerciseId;
        String exerciseName;
        ExerciseViewHolderListener listener;

        ExerciseViewHolder(View itemView, ExerciseViewHolderListener listener) {
            super(itemView);
            exerciseNameView = itemView.findViewById(R.id.exercise_name);
            exerciseImgView = itemView.findViewById(R.id.exercise_img);
            this.listener = listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("ID " + exerciseId + " NAME " + exerciseName);
            listener.onExerciseClick(exerciseId, exerciseName);
        }
    }
}