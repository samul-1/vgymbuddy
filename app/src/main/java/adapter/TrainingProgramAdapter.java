package adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;

public class TrainingProgramAdapter extends AbstractCursorAdapter<TrainingProgramAdapter.TrainingProgramViewHolder>{
    public TrainingProgramAdapter(Cursor c) {
        super(c);
    }
    public TrainingProgramAdapter() {
        super(null);
    }
    @Override
    public TrainingProgramAdapter.TrainingProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.program_card, parent, false);
        return new TrainingProgramAdapter.TrainingProgramViewHolder(formNameView);
    }
    @Override
    public void onBindViewHolder(TrainingProgramAdapter.TrainingProgramViewHolder holder, Cursor cursor) {
        // fetch program's data
        String programName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String programDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        holder.programNameView.setText(programName);
        holder.programDescriptionView.setText(programDescription);
    }
    @Override
    public void swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);
    }

    class TrainingProgramViewHolder extends RecyclerView.ViewHolder {
        TextView programNameView;
        TextView programDescriptionView;
        TrainingProgramViewHolder(View itemView) {
            super(itemView);
            programNameView = itemView.findViewById(R.id.program_name);
            programDescriptionView = itemView.findViewById(R.id.program_description);
        }
    }
}