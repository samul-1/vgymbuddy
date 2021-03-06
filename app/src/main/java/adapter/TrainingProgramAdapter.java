package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.prefs.Preferences;

import it.bsamu.sam.virtualgymbuddy.R;

public class TrainingProgramAdapter extends AbstractCursorAdapter<TrainingProgramAdapter.TrainingProgramViewHolder>{
    /**
     * Handles clicks and long clicks on a training program viewholder
     */
    public interface TrainingProgramViewHolderListener {
        void navigateToProgramDetails(long programId);
        void setActiveTrainingProgram(long programId);
    }

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

        long activeProgramId = parent.getContext()
                .getSharedPreferences(
                        parent.getContext().getString(R.string.pref_file_key),
                        Context.MODE_PRIVATE
                )
                .getLong(parent.getContext().getString(R.string.active_program_pref_key), 0L);

        return new TrainingProgramAdapter.TrainingProgramViewHolder(formNameView, listener, activeProgramId);
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

        // show active chip if item is the active program
        holder.setActiveChipVisibility(
                holder.prefs.getLong(holder.activeProgramKey, 0L) == holder.programId
                        ? View.VISIBLE : View.INVISIBLE
        );

        // make holder accessible from recycler view - used to change chip
        // visibility if the active program is changed later
        holder.itemView.setTag(holder.programId);
    }



    class TrainingProgramViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView programNameView, programDescriptionView;
        Button editBtn;
        Chip activeChip;
        long programId;
        SharedPreferences prefs;
        String activeProgramKey;

        TrainingProgramViewHolderListener listener;

        TrainingProgramViewHolder(View itemView, TrainingProgramViewHolderListener listener, long activeProgramId) {
            super(itemView);
            programNameView = itemView.findViewById(R.id.program_name);
            programDescriptionView = itemView.findViewById(R.id.program_description);
            editBtn = itemView.findViewById(R.id.edit_program_btn);
            activeChip = itemView.findViewById(R.id.active_program_chip);
            this.listener = listener;

            itemView.setOnLongClickListener(this);

            prefs = itemView
                    .getContext()
                    .getSharedPreferences(
                            itemView.getContext().getString(R.string.pref_file_key),
                            Context.MODE_PRIVATE
                    );
            activeProgramKey = itemView.getContext().getString(R.string.active_program_pref_key);
        }

        @Override
        public void onClick(View view) {
            listener.navigateToProgramDetails(programId);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.setActiveTrainingProgram(programId);
            return true;
        }

        public void setActiveChipVisibility(int visibility) {
            activeChip.setVisibility(visibility);
        }
    }
}