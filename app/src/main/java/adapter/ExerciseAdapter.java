package adapter;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import it.bsamu.sam.virtualgymbuddy.R;

public class ExerciseAdapter extends AbstractCursorAdapter<ExerciseAdapter.ExerciseViewHolder> {
    public ExerciseAdapter(Cursor c) {
        super(c);
    }
    public ExerciseAdapter() {
        super(null);
    }
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exercise_card, parent, false);
        return new ExerciseViewHolder(formNameView);
    }
    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, Cursor cursor) {
        // fetch exercise's data
        String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        long exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

        holder.exerciseNameView.setText(exerciseName);

        File imgFile = new File(
                holder.exerciseNameView.getContext().getFilesDir(),
                String.valueOf(exerciseId) + ".jpg"
        );
        // if the exercise has an image associated to it, set it as card preview
        if(imgFile.exists()) {
            Uri exerciseImgUri = Uri.fromFile(
                    imgFile
            );
            System.out.println("FOUND IMAGE FOR " + exerciseName + ": " + exerciseImgUri);
            holder.exerciseImgView.setImageURI(exerciseImgUri);
        } else {
            System.out.println("no image for " + exerciseName);
        }

    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameView;
        ImageView exerciseImgView;
        ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseNameView = itemView.findViewById(R.id.exercise_name);
            exerciseImgView = itemView.findViewById(R.id.exercise_img);
        }
    }
}