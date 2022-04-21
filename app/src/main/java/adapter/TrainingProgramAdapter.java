package adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.bsamu.sam.virtualgymbuddy.R;

public class TrainingProgramAdapter extends AbstractCursorAdapter<TrainingProgramAdapter.TrainingProgramViewHolder>{
    private TrainingProgramViewHolderListener listener;

    public TrainingProgramAdapter(TrainingProgramViewHolderListener listener) {
        super(null);
        this.listener = listener;
    }
    @Override
    public TrainingProgramAdapter.TrainingProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.program_item, parent, false);
        return new TrainingProgramAdapter.TrainingProgramViewHolder(formNameView, listener);
    }
    @Override
    public void onBindViewHolder(TrainingProgramAdapter.TrainingProgramViewHolder holder, Cursor cursor) {
        // fetch program's data
        String programName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String programDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        holder.programId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

        holder.programNameView.setText(programName);
        holder.programDescriptionView.setText(programDescription);
        holder.editBtn.setOnClickListener(holder);
    }

    public interface TrainingProgramViewHolderListener {
        public void navigateToProgramDetails(long programId);
    }

    class TrainingProgramViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView programNameView;
        TextView programDescriptionView;
        Button editBtn;
        long programId;
        TrainingProgramViewHolderListener listener;
        TrainingProgramViewHolder(View itemView, TrainingProgramViewHolderListener listener) {
            super(itemView);
            programNameView = itemView.findViewById(R.id.program_name);
            programDescriptionView = itemView.findViewById(R.id.program_description);
            editBtn = itemView.findViewById(R.id.edit_program_btn);
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.navigateToProgramDetails(programId);
        }
    }
}