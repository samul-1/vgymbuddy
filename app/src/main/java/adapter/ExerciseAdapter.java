package adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.Exercise;

public class ExerciseAdapter extends /*ArrayAdapter<Exercise>*/ CursorAdapter {
    public ExerciseAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.exercise_card, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = view.findViewById(R.id.exercise_name);
        ImageView iv = view.findViewById(R.id.exercise_img);

        String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        long exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

        tv.setText(exerciseName);

        File imgFile = new File(context.getFilesDir(), String.valueOf(exerciseId) + ".jpg");
        // if the exercise has an image associated to it, set it as card preview
        if(imgFile.exists()) {
            Uri exerciseImgUri = Uri.fromFile(
                    imgFile
            );
            System.out.println("FOUND IMAGE FOR " + exerciseName + ": " + exerciseImgUri);
            iv.setImageURI(exerciseImgUri);
        } else {
            System.out.println("no image for " + exerciseName);
        }
    }
}
