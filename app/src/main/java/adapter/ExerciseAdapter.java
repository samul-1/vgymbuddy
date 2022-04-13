package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.Exercise;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {
    public ExerciseAdapter(@NonNull Context context, int resource, @NonNull List<Exercise> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Exercise item = getItem(position);
        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.exercise_card, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.exercise_name);
        tv.setText(item.name);
        return convertView;
        //return super.getView(position, convertView, parent);
    }
}
