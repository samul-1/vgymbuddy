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
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;

import adapter.ExerciseAdapter;
import it.bsamu.sam.virtualgymbuddy.MainActivity;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.AppDb;
import relational.entities.Exercise;

public class ExerciseCreationDialog extends DialogFragment implements View.OnClickListener {
    private ExerciseCreationDialogListener listener;
    private EditText nameInput;
    private ImageView previewView;
    private Button pickImgBtn;
    private Uri pickedImg;

    private final int PICK_IMAGE = 100;


    @Override
    public void onClick(View view) {
        System.out.println("CLICKED" + view);
        if (view==pickImgBtn) {
            openGallery();
            return;
        }
        throw new AssertionError();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode==PICK_IMAGE) {
            Uri imageUri = data.getData();
            System.out.println("URI" + imageUri);
            previewView.setImageURI(imageUri);
            pickedImg = imageUri;
        }
    }

    public interface ExerciseCreationDialogListener {
        public void onCreateExercise(DialogFragment dialog, String exerciseName, Uri pickedImgUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // used to send events to dialog host
            listener = (ExerciseCreationDialogListener)
                    ((MainActivity)context)
                            .getSupportFragmentManager()
                            .findFragmentByTag("f1"); // TODO find a less ugly way
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + "," +getActivity().toString()
                    + " must implement ExerciseCreationDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.exercise_creation_dialog, null);

        previewView = view.findViewById(R.id.dialog_exercise_img_preview);
        pickImgBtn = view.findViewById(R.id.dialog_exercise_img_btn);
        pickImgBtn.setOnClickListener(this);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCreateExercise(
                                ExerciseCreationDialog.this,
                                ((EditText)getDialog().
                                        findViewById(R.id.exercise_name_input))
                                        .getText().toString(),
                                pickedImg
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExerciseCreationDialog.this.getDialog().cancel();
                    }
                }).setTitle(R.string.dialog_exercise_title);
        return builder.create();
    }
}
