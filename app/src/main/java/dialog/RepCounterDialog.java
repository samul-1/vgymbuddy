package dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.bsamu.sam.virtualgymbuddy.R;
import util.RepCounter;

public class RepCounterDialog extends DialogFragment implements RepCounter.RepCounterListener {
    TextView repCountView;
    RepCounterDialogListener listener;

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
                    RepCounter.getInstance(getActivity()).stopCounting();
                    listener.onStopSet(
                            Integer.valueOf(repCountView.getText().toString())
                    );
                }
        );

        RepCounter
                .getInstance(getActivity())
                .setListener(this)
                .startCounting();
        System.out.println("started counting");

        return builder.create();
    }

}
