package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import adapter.TrainingSessionSetAdapter;
import dialog.RepCounterDialog;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.databinding.TodaysTrainingFragmentBinding;
import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingSession;
import relational.entities.TrainingSessionSet;
import viewmodel.CurrentTrainingSessionViewModel;

public class CurrentProgramFragment extends AbstractCursorRecyclerViewFragment<TrainingSessionSetAdapter> implements View.OnClickListener, Runnable, RepCounterDialog.RepCounterDialogListener {
    short todayWeekDayIdx;
    short currentRestTime;
    short remainingRestTime;
    long activeProgramId;
    Map<Exercise, List<TrainingSessionSet>> exercisesWithSets;
    TrainingDay trainingDay;
    List<TrainingDayExercise> trainingDayExercises;
    TrainingSession session;
    Exercise currentExercise;
    List<TrainingSessionSet> currentExerciseSets = new LinkedList<>();
    EditText repsInput;
    EditText weightInput;
    Button addSetBtn, recordSetBtn, countRepsBtn;
    ImageView videoThumbnail;
    TextView restTimerText;
    ViewGroup restTimeLayout, setAuxButtonsLayout;
    TextView currentExerciseTextView;
    Uri takenVideoUri;
    RepCounterDialog repCounterDialog;

    Handler restTimerHandler = new Handler();

    static final int REQUEST_VIDEO_CAPTURE = 22222;

    CurrentTrainingSessionViewModel viewModel;


    public CurrentProgramFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel =
                ViewModelProviders
                        .of(requireActivity())
                        .get(CurrentTrainingSessionViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("calling on viewcreated");
        repsInput = view.findViewById(R.id.training_session_reps_input);
        weightInput = view.findViewById(R.id.training_session_weight_input);
        addSetBtn = view.findViewById(R.id.add_set_btn);
        countRepsBtn = view.findViewById(R.id.set_count_reps_btn);
        recordSetBtn = view.findViewById(R.id.set_record_video_btn);
        videoThumbnail = view.findViewById(R.id.set_video_thumbnail);
        restTimerText = view.findViewById(R.id.rest_timer_text);
        restTimeLayout = view.findViewById(R.id.rest_timer_container);
        setAuxButtonsLayout = view.findViewById(R.id.set_aux_btn_container);
        currentExerciseTextView = view.findViewById(R.id.session_current_exercise);

        recordSetBtn.setOnClickListener(this);
        countRepsBtn.setOnClickListener(this);
        addSetBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        // get information needed to fetch today's training
        todayWeekDayIdx = (short)LocalDate.now().getDayOfWeek().getValue();
        activeProgramId = getContext().getSharedPreferences(
                getString(R.string.pref_file_key), Context.MODE_PRIVATE
        ).getLong(getString(R.string.active_program_pref_key), 0L);
        super.onResume();
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                fetchTrainingDay();
                if(trainingDay != null) {
                    fetchOrCreateTodaysTrainingSession();
                    fetchExercisesAndSets();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                paintTrainingSessionInfo(true);
            }
        }.execute();
    }


    @Override
    protected TrainingSessionSetAdapter getAdapter() {
        return new TrainingSessionSetAdapter(currentExerciseSets);
    }

    @Override
    protected RecyclerView getRecyclerView(View parent) {
        return parent.findViewById(R.id.todays_training_recyclerview);
    }

    @Override
    protected View getMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.todays_training_fragment, container,false);
    }

    private void fetchExercisesAndSets() {
        Map<Exercise, List<TrainingSessionSet>> exercisesWithSets = db
                .trainingSessionSetDao()
                .getExercisesWithSetsForSession(session.id);

        viewModel.setExercisesWithSets(exercisesWithSets);

    }

    private synchronized void fetchOrCreateTodaysTrainingSession() {
        // attempts to retrieve a training session that matches the current
        // training day; if it doesn't exist, it creates one in the database
       TrainingSession session = db
                .trainingSessionDao()
                .getTodaysTrainingSessionForTrainingDay(trainingDay.id);
        long sessionId;
        if(session == null) {
            sessionId = db
                    .trainingSessionDao()
                    .insertTrainingSession(
                            new TrainingSession(
                                trainingDay.id, new Date()
                            )
                    );
            session = db.trainingSessionDao().getById(sessionId);
        } else {
            sessionId = session.id;
        }
        viewModel.setSessionId(sessionId);

        System.out.println("SESSION " + session);
    }

    private void fetchTrainingDay() {
        System.out.println("FETCHING " + activeProgramId);

        TrainingDay trainingDay = db
                .trainingDayDao()
                .getForProgramAndDayOfWeek(activeProgramId, todayWeekDayIdx);
        viewModel.setTrainingDayId(trainingDay.id);

        if(trainingDay != null) {
           List<TrainingDayExercise> trainingDayExercises = db
                   .trainingDayDao()
                   .getExercisesFor(trainingDay.id);
           viewModel.setTrainingDayExercises(trainingDayExercises);
        }
    }

    @Override
    public void onStopSet(int repCount) {
        //repCounterDialog.dismiss();
        repsInput.setText(String.valueOf(repCount));
    }


    enum EmptyStates {
        NO_ACTIVE_PROGRAM,
        REST_DAY,
        FINISHED
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                .putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                );
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            // TODO show message camera not available
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            // save video uri for later usage (when saving data for the set to db)
            takenVideoUri = intent.getData();

            // hide auxiliary buttons
            setAuxButtonsLayout.setVisibility(View.GONE);

            // show video thumbnail
            Glide
                .with(getActivity())
                .load(takenVideoUri)
                .into(videoThumbnail);
            videoThumbnail.setVisibility(View.VISIBLE);
        }
    }

    private void paintEmptyState(EmptyStates state) {
        View emptyStateView = getActivity().findViewById(R.id.training_session_empty_state_container);
        View controlsView = getActivity().findViewById(R.id.training_session_controls_container);
        // TODO this gets called even if not in this fragment (move to onResume etc.)
        emptyStateView.setVisibility(View.VISIBLE);
        // hide controls
        controlsView.setVisibility(View.GONE);
        getRecyclerView(getView()).setVisibility(View.GONE);


        int iconResId, textResId;

        ImageView iv = (ImageView) getActivity().findViewById(R.id.training_session_empty_state_icon);
        TextView tv = (TextView) getActivity().findViewById(R.id.training_session_empty_state_desc);

        switch(state) {
            case NO_ACTIVE_PROGRAM:
                iconResId = R.drawable.ic_baseline_sentiment_very_dissatisfied_24;
                textResId = R.string.no_active_program;
                break;
            case REST_DAY:
                iconResId = R.drawable.ic_baseline_single_bed_24;
                textResId = R.string.rest_day;
                break;
            case FINISHED:
                iconResId = R.drawable.ic_baseline_done_24;
                textResId = R.string.done_training;
                break;
            default:
                throw new AssertionError();
        }
        iv.setImageResource(iconResId);
        tv.setText(textResId);
    }


    private void paintTrainingSessionInfo(boolean updateCurrentExercise) {
        if(activeProgramId == 0L) {
            paintEmptyState(EmptyStates.NO_ACTIVE_PROGRAM);
        } else if (session == null) {
            paintEmptyState(EmptyStates.REST_DAY);
        } else {
            // FIXME use viewmodel
            // hide empty state
            View emptyStateView = getActivity().findViewById(R.id.training_session_empty_state_container);
            View controlsView = getActivity().findViewById(R.id.training_session_controls_container);
            emptyStateView.setVisibility(View.GONE);
            // show controls
            controlsView.setVisibility(View.VISIBLE);
            getRecyclerView(getView()).setVisibility(View.VISIBLE);

            if(updateCurrentExercise) {
                viewModel.updateCurrentExercise();
            }
            viewModel.setDataSetToCurrentExerciseSets();
            if(currentExercise != null) {
                // FIXME use viewmodel
                currentExerciseTextView
                        .setText(currentExercise.name);
            } else {
                // FIXME use viewmodel
                paintEmptyState(EmptyStates.FINISHED);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v == addSetBtn) {
            short reps = Short.valueOf(repsInput.getText().toString());
            double weight = Double.valueOf(weightInput.getText().toString());

            System.out.println("INSERTING REPS " + reps + " WEIGHT " + weight);

            insertTrainingSet(reps, weight);
        } else if(v==recordSetBtn) {
            dispatchTakeVideoIntent();
        } else if (v==countRepsBtn) {
            countReps();
        } else {
            System.out.println("?????" + v);
        }
    }

    private void countReps() {
        repCounterDialog = repCounterDialog == null ?
                new RepCounterDialog(this) :
                repCounterDialog;
        repCounterDialog.show(getActivity().getSupportFragmentManager(), "rep-counter-dialog");
    }

    private void insertTrainingSet(short reps, double weight) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                TrainingSessionSet set = new TrainingSessionSet(
                        currentExercise.id,
                        viewModel.getSessionId(),
                        reps,
                        weight,
                        takenVideoUri
                );
                db.trainingSessionSetDao().insertSet(set);
                fetchExercisesAndSets();
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                paintTrainingSessionInfo(false);
                startNextSetTimer();
            }
        }.execute();
    }


    private void updateRestTimer() {
        getActivity().runOnUiThread(() ->
        {
            restTimerText.setText(String.valueOf(remainingRestTime--));

            if (remainingRestTime < 0) {
                // FIXME use viewmodel
                restTimeLayout.setVisibility(View.GONE);
                setControlsLayoutEnabled(true);
                viewModel.updateCurrentExercise();
                viewModel.setDataSetToCurrentExerciseSets();
                paintTrainingSessionInfo(true);
            } else {
                restTimerHandler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void run() {
        updateRestTimer();
    }

    private void setControlsLayoutEnabled(boolean enabled) {
        // TODO refactor to make recursive
        // TODO extract in a Utils class
        LinearLayout layout = getActivity().findViewById(R.id.training_session_controls_container);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(enabled);
        }
    }


    private void startNextSetTimer() {
       restTimeLayout.setVisibility(View.VISIBLE);
       videoThumbnail.setVisibility(View.GONE);
       setControlsLayoutEnabled(false);
        remainingRestTime = currentRestTime;
        restTimerHandler.postDelayed(this, 0);
    }
}