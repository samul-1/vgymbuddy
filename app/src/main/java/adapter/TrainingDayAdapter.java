package adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.TrainingDay;

public class TrainingDayAdapter extends RecyclerView.Adapter<TrainingDayAdapter.TrainingDayViewHolder> {
    protected TrainingDayViewHolderListener listener;
    private Context context;
    List<TrainingDay> dataset;

    public TrainingDayAdapter(TrainingDayViewHolderListener listener, Context context, List<TrainingDay> dataset) {
        this.dataset = dataset;
        this.listener = listener;
        this.context = context;
        System.out.println("instantiating adapter");
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface TrainingDayViewHolderListener {
        void navigateToTrainingDayDetails(long dayId);
    }

    @Override
    public void onBindViewHolder(TrainingDayAdapter.TrainingDayViewHolder holder, int position) {
        TrainingDay day = dataset.get(position);
        short dayOfWeekIdx = day.dayOfWeek;
        long dayId = day.id;

        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        holder.trainingDayPosition.setText(daysOfWeek[dayOfWeekIdx-1]);
        holder.itemView.setOnClickListener((__)->listener.navigateToTrainingDayDetails(dayId));
    }

    @NonNull
    @Override
    public TrainingDayAdapter.TrainingDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.training_day_item, parent, false);
        return new TrainingDayAdapter.TrainingDayViewHolder(formNameView);
    }

    class TrainingDayViewHolder extends RecyclerView.ViewHolder {
        TextView trainingDayPosition;
        TrainingDayViewHolder(View itemView) {
            super(itemView);
            trainingDayPosition = itemView.findViewById(R.id.training_day_position);
        }
    }
}
