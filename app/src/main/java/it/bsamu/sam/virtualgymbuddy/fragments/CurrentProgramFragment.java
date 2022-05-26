package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.io.Serializable;
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

    EditText repsInput;
    EditText weightInput;
    Button addSetBtn, recordSetBtn, countRepsBtn;
    ImageView videoThumbnail;
    ViewGroup setAuxButtonsLayout;
    Uri takenVideoUri;
    RepCounterDialog repCounterDialog;

    Handler restTimerHandler = new Handler();

    private final String BUNDLE_THUMBNAIL_IMG_KEY = "video_thumbnail";

    static final int REQUEST_VIDEO_CAPTURE = 22222;

    CurrentTrainingSessionViewModel viewModel;
    private TodaysTrainingFragmentBinding binding;

    public CurrentProgramFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(
                        inflater,
                        R.layout.todays_training_fragment,
                        container,
                        false
                );
        binding.setLifecycleOwner(getActivity());

        // set up view model
        viewModel = ViewModelProviders
                        .of(this)
                        .get(CurrentTrainingSessionViewModel.class);

        // set up recycler view to use the root from binding
        recyclerView = getRecyclerView(binding.getRoot());
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setAdapter(getAdapter());

        // observe dataset of the recyclerview
        Observer<List<TrainingSessionSet>> setsObserver =
            trainingSessionSets -> {
                adapter.update(trainingSessionSets);
            };
        viewModel.getCurrentExerciseSets().observe(getActivity(), setsObserver);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repsInput = view.findViewById(R.id.training_session_reps_input);
        weightInput = view.findViewById(R.id.training_session_weight_input);
        addSetBtn = view.findViewById(R.id.add_set_btn);
        countRepsBtn = view.findViewById(R.id.set_count_reps_btn);
        recordSetBtn = view.findViewById(R.id.set_record_video_btn);
        videoThumbnail = view.findViewById(R.id.set_video_thumbnail);
        setAuxButtonsLayout = view.findViewById(R.id.set_aux_btn_container);

        recordSetBtn.setOnClickListener(this);
        countRepsBtn.setOnClickListener(this);
        addSetBtn.setOnClickListener(this);

        binding.setViewmodel(viewModel);

        // restore video thumbnail, if present
        if(savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_THUMBNAIL_IMG_KEY)) {
            videoThumbnail.setImageBitmap((Bitmap) savedInstanceState
                    .getParcelable(BUNDLE_THUMBNAIL_IMG_KEY)
            );
            videoThumbnail.setVisibility(View.VISIBLE);
            setAuxButtonsLayout.setVisibility(View.GONE);
        }
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
        super.onResume();
    }

    @Override
    protected void asyncFetchMainEntity() {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                long fetchedDayId = fetchTrainingDay();
                if(fetchedDayId != 0L) {
                    long sessionId = fetchOrCreateTodaysTrainingSession(fetchedDayId);
                    fetchExercisesAndSets(sessionId);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                viewModel.updateCurrentExercise();
            }
        }.execute();
    }


    @Override
    protected TrainingSessionSetAdapter instantiateAdapter() {
        return new TrainingSessionSetAdapter(viewModel.getCurrentExerciseSets().getValue());
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
                .getForProgramAndDayOfWeek(
                        viewModel.getActiveProgramId().getValue(),
                        todayWeekDayIdx
                );

        if(trainingDay != null) {
            viewModel.setTrainingDayId(trainingDay.id);
            List<TrainingDayExercise> trainingDayExercises = db
                   .trainingDayDao()
                   .getExercisesFor(trainingDay.id);
            viewModel.setTrainingDayExercises(trainingDayExercises);
        }

        return trainingDay == null ? 0L : trainingDay.id;
    }

    @Override
    public void onStopSet(int repCount) {
        repsInput.setText(String.valueOf(repCount));
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        BitmapDrawable thumbnailDrawable = ((BitmapDrawable)videoThumbnail.getDrawable());
        if(thumbnailDrawable != null) {
            outState.putParcelable(BUNDLE_THUMBNAIL_IMG_KEY,
                    (Parcelable) thumbnailDrawable.getBitmap()
            );
        }
    }

    @Override
    public void onClick(View v) {
        if(v == addSetBtn) {
            short reps = Short.valueOf(repsInput.getText().toString());
            double weight = Double.valueOf(weightInput.getText().toString());
            insertTrainingSet(reps, weight);
        } else if(v == recordSetBtn) {
            dispatchTakeVideoIntent();
        } else if (v == countRepsBtn) {
            countReps();
        } else {
            throw new AssertionError();
        }
    }

    private void countReps() {
        repCounterDialog = new RepCounterDialog();
        repCounterDialog.show(getChildFragmentManager(), "rep-counter-dialog");
    }

    private void insertTrainingSet(short reps, double weight) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                TrainingSessionSet set = new TrainingSessionSet(
                        viewModel.getCurrentExercise().getValue().id,
                        viewModel.getSessionId().getValue(),
                        reps,
                        weight,
                        takenVideoUri,
                        new Date()
                );
                db.trainingSessionSetDao().insertSet(set);
                viewModel.addCurrentExerciseSet(set);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                startNextSetTimer();
            }
        }.execute();
    }


    private void updateRestTimer() {
        short remainingRestTime = viewModel.getRemainingRestTime().getValue();
            viewModel.setRemainingRestTime(
                    (short)(remainingRestTime-1)
            );

            if (remainingRestTime-1 == 0) {
                // time's up, on to the next exercise
                viewModel.updateCurrentExercise();
            } else {
                restTimerHandler.postDelayed(this, 1000);
            }
    }

    @Override
    public void run() {
        updateRestTimer();
    }


    private void startNextSetTimer() {
       videoThumbnail.setVisibility(View.GONE);
       // start rest timer
       viewModel.setRemainingRestTime(viewModel.getCurrentRestTime().getValue());
       restTimerHandler.postDelayed(this, 1000);
    }

    @BindingAdapter("app:visibleIf")
    public static void visibleIf(View view, Boolean condition) {
        view.setVisibility(condition ? View.VISIBLE : View.GONE);
    }
}