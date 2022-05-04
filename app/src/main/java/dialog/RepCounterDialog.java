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
    TextView repCountView;
    RepCounterDialogListener listener;
    ImageView phoneIcon, weightStackIcon;

    @Override
    public void onRep() {
        int currentCount = Integer.valueOf(repCountView.getText().toString());
        repCountView.setText(String.valueOf(currentCount+1));
    }

    public interface RepCounterDialogListener {
        void onStopSet(int repCount);
    }

    public RepCounterDialog(RepCounterDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        terminateSet();
    }

    private void terminateSet() {
        RepCounter.getInstance(getActivity()).stopCounting();
        listener.onStopSet(
                Integer.valueOf(repCountView.getText().toString())
        );
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
                (dialog, id) -> {
                    terminateSet();
                }
        );

        phoneIcon = view.findViewById(R.id.phone_icon);
        weightStackIcon = view.findViewById(R.id.weight_icon);

        RepCounter
                .getInstance(getActivity())
                .setListener(this)
                .startCounting();


        animateIcons();


        return builder.create();
    }

    private void animateIcons() {
        new Handler().postDelayed(() -> {
            TranslateAnimation anim = new TranslateAnimation(
                    phoneIcon.getLeft(),
                    (float)(weightStackIcon.getLeft()-phoneIcon.getWidth()/2.8),
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
