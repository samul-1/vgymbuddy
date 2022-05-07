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
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
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
import it.bsamu.sam.virtualgymbuddy.viewmodel.CurrentTrainingSessionViewModel;

public class CurrentProgramFragment extends AbstractCursorRecyclerViewFragment<TrainingSessionSetAdapter> implements View.OnClickListener, Runnable, RepCounterDialog.RepCounterDialogListener {
    short todayWeekDayIdx;
    TrainingDay trainingDay;
    TrainingSession session;
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
    private TodaysTrainingFragmentBinding binding;


    public CurrentProgramFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil
                .inflate(
                        inflater,
                        R.layout.todays_training_fragment,
                        container,
                        false
                );
        binding.setLifecycleOwner(this);

        viewModel = ViewModelProviders
                        .of(this)
                        .get(CurrentTrainingSessionViewModel.class);

        return binding.getRoot();
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

        binding.setViewmodel(viewModel);

        binding.setLifecycleOwner(this);
        //viewModel.setActiveProgramId(12L);
    }

    @Override
    public void onResume() {
        // get information needed to fetch today's training
        todayWeekDayIdx = (short)LocalDate.now().getDayOfWeek().getValue();

        // get active program id from preferences and set it in view model
        viewModel
                .setActiveProgramId(
                   getContext()
                       .getSharedPreferences(
                                getString(R.string.pref_file_key),
                                Context.MODE_PRIVATE
                        )
                        .getLong(getString(R.string.active_program_pref_key), 0L)
        );
        System.out.println("set active program id" + viewModel.getActiveProgramId().getValue());
        super.onResume();
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                System.out.println("!!!!!!! FETCH MAIN");
                long fetchedDayId = fetchTrainingDay();
                System.out.println("fetched day, id in viewmodel is " + viewModel.getTrainingDayId().getValue());
                if(fetchedDayId != 0L) {
                    long sessionId = fetchOrCreateTodaysTrainingSession(fetchedDayId);
                    fetchExercisesAndSets(sessionId);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                updateTrainingSessionInfo(true);
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

    private void fetchExercisesAndSets(long sessionId) {
        Map<Exercise, List<TrainingSessionSet>> exercisesWithSets = db
                .trainingSessionSetDao()
                .getExercisesWithSetsForSession(sessionId);

        viewModel.setExercisesWithSets(exercisesWithSets);

    }

    private synchronized long fetchOrCreateTodaysTrainingSession(long trainingDayId) {
        // attempts to retrieve a training session that matches the current
        // training day; if it doesn't exist, it creates one in the database
        System.out.println("fetching sess id");
       TrainingSession session = db
                .trainingSessionDao()
                .getTodaysTrainingSessionForTrainingDay(trainingDayId);
        long sessionId;
        if(session == null) {
            sessionId = db
                    .trainingSessionDao()
                    .insertTrainingSession(
                            new TrainingSession(
                                trainingDayId, new Date()
                            )
                    );
        } else {
            sessionId = session.id;
        }
        viewModel.setSessionId(sessionId);
        return sessionId;
    }
    private long fetchTrainingDay() {
        TrainingDay trainingDay = db
                .trainingDayDao()
                .getForProgramAndDayOfWeek(viewModel.getActiveProgramId().getValue(), todayWeekDayIdx);

        if(trainingDay != null) {
            viewModel.setTrainingDayId(trainingDay.id);
            List<TrainingDayExercise> trainingDayExercises = db
                   .trainingDayDao()
                   .getExercisesFor(trainingDay.id);
            viewModel.setTrainingDayExercises(trainingDayExercises);
        } else {
            System.out.println("DAY IS NULL for id, day: " + viewModel.getActiveProgramId().getValue() + " " + todayWeekDayIdx);
        }
        return trainingDay == null ? 0 : trainingDay.id;
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


    private void updateTrainingSessionInfo(boolean updateCurrentExercise) {
            if(updateCurrentExercise) {
                viewModel.updateCurrentExercise();
            }
            System.out.println("ADAPTER IS  " + getAdapter());
            viewModel.setDataSetToCurrentExerciseSets(adapter);
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
                        viewModel.getCurrentExercise().id,
                        viewModel.getSessionId().getValue(),
                        reps,
                        weight,
                        takenVideoUri
                );
                db.trainingSessionSetDao().insertSet(set);
                fetchExercisesAndSets(viewModel.getSessionId().getValue());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                updateTrainingSessionInfo(false);
                startNextSetTimer();
            }
        }.execute();
    }


    private void updateRestTimer() {
        //getActivity().runOnUiThread(() ->
        //{
            viewModel.setRemainingRestTime(
                    (short)(viewModel.getRemainingRestTime().getValue()-1)
            );

            if (viewModel.getRemainingRestTime().getValue() < 0) {
                viewModel.updateCurrentExercise();
                viewModel.setDataSetToCurrentExerciseSets(adapter);
                updateTrainingSessionInfo(true);
            } else {
                restTimerHandler.postDelayed(this, 1000);
            }
        //});
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
        viewModel.setRemainingRestTime(viewModel.getCurrentRestTime().getValue());
        restTimerHandler.postDelayed(this, 0);
    }

    @BindingAdapter("app:visibleIf")
    public static void visibleIf(View view, Boolean condition) {
        view.setVisibility(condition ? View.VISIBLE : View.GONE);
    }
}