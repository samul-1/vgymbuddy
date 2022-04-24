package adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.TrainingSessionSet;

public class TrainingSessionSetAdapter extends RecyclerView.Adapter<TrainingSessionSetAdapter.TrainingSessionSetViewHolder> {
    List<TrainingSessionSet> dataset;

    public TrainingSessionSetAdapter(List<TrainingSessionSet> sets) {
       dataset = sets;
    }


    @NonNull
    @Override
    public TrainingSessionSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.training_session_set_item, parent, false);
        return new TrainingSessionSetAdapter.TrainingSessionSetViewHolder(formNameView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingSessionSetViewHolder holder, int position) {
        TrainingSessionSet set = dataset.get(position);

        TextView repsView = holder.itemView.findViewById(R.id.set_reps);
        TextView weightView = holder.itemView.findViewById(R.id.set_weight);

        repsView.setText(String.valueOf(set.repsDone));
        weightView.setText(String.valueOf(set.weightUsed));
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    class TrainingSessionSetViewHolder extends RecyclerView.ViewHolder {
        public TrainingSessionSetViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
