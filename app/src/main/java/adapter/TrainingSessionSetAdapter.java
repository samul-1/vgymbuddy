package adapter;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import relational.entities.TrainingSessionSet;

public class TrainingSessionSetAdapter extends RecyclerView.Adapter<TrainingSessionSetAdapter.TrainingSessionSetViewHolder> {

    List<TrainingSessionSet> dataset;
    public TrainingSessionSetAdapter(List<TrainingSessionSet> sets) {
       dataset = sets;
    }


    @NonNull
    @Override
    public TrainingSessionSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingSessionSetViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class TrainingSessionSetViewHolder extends RecyclerView.ViewHolder {
        public TrainingSessionSetViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
