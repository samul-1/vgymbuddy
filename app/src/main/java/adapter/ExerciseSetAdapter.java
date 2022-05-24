package adapter;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import it.bsamu.sam.virtualgymbuddy.R;

public class ExerciseSetAdapter extends AbstractCursorAdapter<ExerciseSetAdapter.ExerciseSetViewHolder>{

    public ExerciseSetAdapter() {
        super(null);
    }


    @Override
    public void onBindViewHolder(ExerciseSetViewHolder holder, Cursor cursor) {
        // fetch set data
        short reps = cursor.getShort(cursor.getColumnIndexOrThrow("repsDone"));
        double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weightUsed"));
        Date sessionTimestamp = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
        String videoUriString = cursor.getString(cursor.getColumnIndexOrThrow("videoUri"));

        holder.repsView.setText(String.valueOf(reps));
        holder.weightView.setText(String.valueOf(weight));
        holder.timestampView.setText(DateFormat.getDateInstance().format(sessionTimestamp));

        if(videoUriString.length()>0) {
            Uri videoUri = Uri.parse(videoUriString);

            holder.videoView.setVideoURI(videoUri);
            holder.videoView.setOnPreparedListener((mp)-> {
                mp.setVolume(0, 0);
                holder.videoView.seekTo(0);
            });
            holder.videoView.setMediaController(holder.mediaController);
            holder.videoView.start();
        } else {
            throw new AssertionError("no video for " + reps + " " + weight);
        }
    }

    @NonNull
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exercise_set_item, parent, false);


        return new ExerciseSetAdapter.ExerciseSetViewHolder(
                formNameView,
                new MediaController(parent.getContext())
        );
    }

    class ExerciseSetViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView repsView;
        TextView weightView;
        TextView timestampView;
        MediaController mediaController;

        ExerciseSetViewHolder(View itemView, MediaController mediaController) {
            super(itemView);
            videoView = itemView.findViewById(R.id.set_item_video);
            repsView = itemView.findViewById(R.id.set_item_reps);
            weightView = itemView.findViewById(R.id.set_item_weight);
            timestampView = itemView.findViewById(R.id.set_session_timestamp);
            this.mediaController = mediaController;
        }
    }
}
