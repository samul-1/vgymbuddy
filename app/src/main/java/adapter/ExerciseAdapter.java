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
    /*public ExerciseAdapter(@NonNull Context context, int resource, @NonNull List<Exercise> objects) {
        super(context, 0, objects);
    }*/

   /* @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Exercise item = getItem(position);
        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.exercise_card, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.exercise_name);
        tv.setText(item.name);

        File imgFile = new File(getContext().getFilesDir(), String.valueOf(item.id) + ".jpg");
        if(imgFile.exists()) {
            Uri exerciseImgUri = Uri.fromFile(
                imgFile
            );
            // if the exercise has an image associated to it, set it as card preview
            ImageView iv = convertView.findViewById(R.id.exercise_img);
            System.out.println("FOUND IMAGE FOR " + item.name + ": " + exerciseImgUri);
            iv.setImageURI(exerciseImgUri);
        } else {
            System.out.println("no image for " + item.name);
        }
        return convertView;
        //return super.getView(position, convertView, parent);
    }*/

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
        if(imgFile.exists()) {
            Uri exerciseImgUri = Uri.fromFile(
                    imgFile
            );
            // if the exercise has an image associated to it, set it as card preview
            System.out.println("FOUND IMAGE FOR " + exerciseName + ": " + exerciseImgUri);
            iv.setImageURI(exerciseImgUri);
        } else {
            System.out.println("no image for " + exerciseName);
        }
    }
}
