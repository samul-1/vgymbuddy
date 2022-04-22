package adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;
public class TrainingDayAdapter extends AbstractCursorAdapter<TrainingDayAdapter.TrainingDayViewHolder> {
    protected TrainingDayViewHolderListener listener;
    private Context context;

    public TrainingDayAdapter(TrainingDayViewHolderListener listener, Context context) {
        super(null);
        this.listener = listener;
        this.context = context;
        System.out.println("instantiating adapter");
    }



    public interface TrainingDayViewHolderListener {
        void navigateToTrainingDayDetails(long dayId);
    }

    @Override
    public void onBindViewHolder(TrainingDayAdapter.TrainingDayViewHolder holder, Cursor cursor) {
        short dayOfWeekIdx = cursor.getShort(cursor.getColumnIndexOrThrow("dayOfWeek"));
        long dayId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        holder.trainingDayPosition.setText(daysOfWeek[dayOfWeekIdx]);
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
        long dayId;
        TrainingDayViewHolder(View itemView) {
            super(itemView);
            trainingDayPosition = itemView.findViewById(R.id.training_day_position);
        }
    }
}
