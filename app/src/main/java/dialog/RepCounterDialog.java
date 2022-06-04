package dialog;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.bsamu.sam.virtualgymbuddy.R;
import util.RepCounter;

public class RepCounterDialog extends DialogFragment implements RepCounter.RepCounterListener {
    /**
     * Handles stopping the rep counting and getting the rep count
     */
    public interface RepCounterDialogListener {
        void onStopSet(int repCount);
    }

    TextView repCountView;
    ImageView phoneIcon, weightStackIcon;
    int currentRepCount = 0;

    private final String REP_COUNT_KEY = "rep_count";


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(REP_COUNT_KEY, currentRepCount);
    }

    @Override
    public void onRep() {
        currentRepCount++;
        repCountView.setText(String.valueOf(currentRepCount));
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        terminateSet();
    }


    private void terminateSet() {
        // de-register sensor listeners and pass rep count
        RepCounter.getInstance(getActivity()).stopCounting();
        ((RepCounterDialogListener) getParentFragment()).onStopSet(currentRepCount);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rep_counter_dialog, null);

        repCountView = view.findViewById(R.id.rep_count);

        builder.setView(view).setNeutralButton(
                R.string.stop_set,
                (dialog, id) -> terminateSet()
        );

        phoneIcon = view.findViewById(R.id.phone_icon);
        weightStackIcon = view.findViewById(R.id.weight_icon);

        // register sensor listeners and start counting reps
        RepCounter
                .getInstance(getActivity())
                .setListener(this)
                .startCounting();


        animateIcons();

        // restore rep count, if present
        if(savedInstanceState != null && savedInstanceState.containsKey(REP_COUNT_KEY)) {
            currentRepCount = savedInstanceState.getInt(REP_COUNT_KEY);
            repCountView.setText(String.valueOf(currentRepCount));
        }

        return builder.create();
    }

    private void animateIcons() {
        new Handler().postDelayed(() -> {
            double iconOffset = 2.78;

            TranslateAnimation anim = new TranslateAnimation(
                    phoneIcon.getLeft(),
                    (float)(weightStackIcon.getLeft() - phoneIcon.getWidth() / iconOffset),
                    phoneIcon.getTop(),
                    phoneIcon.getTop()
            );
            anim.setDuration(1000);
            anim.setStartOffset(100);
            anim.setFillAfter(true);
            phoneIcon.startAnimation(
                    anim
            );
        }, 100);
    }

}
