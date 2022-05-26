package dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;

import adapter.ExerciseAdapter;
import it.bsamu.sam.virtualgymbuddy.MainActivity;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.AppDb;
import relational.entities.Exercise;

public class TrainingProgramCreationDialog extends DialogFragment {
    public interface TrainingProgramCreationDialogListener {
        public void onCreateProgram(DialogFragment dialog, String programName, String programDescription);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.training_program_creation_dialog, null);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, (dialog, id) ->
                        ((TrainingProgramCreationDialogListener) getParentFragment()).onCreateProgram(
                                TrainingProgramCreationDialog.this,
                                    ((EditText)getDialog().
                                            findViewById(R.id.program_name_input))
                                            .getText().toString(),
                                    ((EditText)getDialog().
                                            findViewById(R.id.program_desc_input))
                                            .getText().toString()
                        )
                )
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        TrainingProgramCreationDialog.this
                                .getDialog().cancel())
                .setTitle(R.string.dialog_program_title);
        return builder.create();
    }
}
