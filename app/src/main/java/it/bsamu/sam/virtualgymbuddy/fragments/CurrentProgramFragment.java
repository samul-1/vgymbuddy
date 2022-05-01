package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import adapter.TrainingSessionSetAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import it.bsamu.sam.virtualgymbuddy.receiver.AlarmReceiver;
import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingSession;
import relational.entities.TrainingSessionSet;

public class CurrentProgramFragment extends AbstractCursorRecyclerViewFragment<TrainingSessionSetAdapter> implements View.OnClickListener, Runnable {
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
    Button addSetBtn;
    Button recordSetBtn;
    VideoView videoView;
    TextView restTimerText;
    TextView currentExerciseTextView;
    Uri takenVideoUri;

    Handler restTimerHandler = new Handler();

    static final int REQUEST_VIDEO_CAPTURE = 22222;


    public CurrentProgramFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get information needed to fetch today's training
        todayWeekDayIdx = (short)LocalDate.now().getDayOfWeek().getValue();
        activeProgramId = getContext().getSharedPreferences(
                getString(R.string.pref_file_key), Context.MODE_PRIVATE
        ).getLong(getString(R.string.active_program_pref_key), 0L);


        /* TODO for each exercise in today session: show input for weight and reps and a button to add
            the set information, until all sets are completed, then move on to the next exercise. For
            each set, also add ability to take video and associate it.
            Start a timer using the exercise rest time after inputting the set info!!!

            to access the videos for an exercise, you can have clicking on the exercise card
            open a dialog that fetches all the TrainingDayExerciseSet with that exerciseId and look
            for videos in the video folder with name "exerciseId_timestamp.mp4" or something*/



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("calling on viewcreated");
        repsInput = view.findViewById(R.id.training_session_reps_input);
        weightInput = view.findViewById(R.id.training_session_weight_input);
        addSetBtn = view.findViewById(R.id.add_set_btn);
        recordSetBtn = view.findViewById(R.id.set_record_video_btn);
        videoView = view.findViewById(R.id.set_video);
        restTimerText = view.findViewById(R.id.rest_timer_text);
        currentExerciseTextView = view.findViewById(R.id.session_current_exercise);

        recordSetBtn.setOnClickListener(this);
        addSetBtn.setOnClickListener(this);

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
        exercisesWithSets = db.trainingSessionSetDao().getExercisesWithSetsForSession(session.id);
    }

    private synchronized void fetchOrCreateTodaysTrainingSession() {
        // attempts to retrieve a training session that matches the current
        // training day; if it doesn't exist, it creates one in the database
        session = db.trainingSessionDao().getTodaysTrainingSessionForTrainingDay(trainingDay.id);
        if(session == null) {
            System.out.println("SESSION IS NULL, CREATING");
            long sessionId = db.trainingSessionDao().insertTrainingSession(new TrainingSession(
                    trainingDay.id, new Date()
            ));
            session = db.trainingSessionDao().getById(sessionId);
        }
        System.out.println("SESSION " + session);
    }

    private void fetchTrainingDay() {
        trainingDay = db.trainingDayDao().getForProgramAndDayOfWeek(activeProgramId, todayWeekDayIdx);
        if(trainingDay != null) {
            trainingDayExercises = db.trainingDayDao().getExercisesFor(trainingDay.id);
        }
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

            // show video preview
            videoView.setVideoURI(takenVideoUri);
            videoView.setVisibility(View.VISIBLE);
            // play mute video
            videoView.seekTo(0);
            videoView.setOnPreparedListener((mp)->mp.setVolume(0,0));
            videoView.start();
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

    private void updateCurrentExercise() {
        /**
         * sets the current exercise instance variable to the first exercise for which
         * not all sets have been completed
         */
        System.out.println("updating exercise");
        for(Map.Entry<Exercise, List<TrainingSessionSet>>entry : exercisesWithSets.entrySet()) {
            TrainingDayExercise exercise = trainingDayExercises
                    .stream()
                    .filter(e->e.exerciseId==entry.getKey().id)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("cannot find exercise " + entry.getKey()));
            long sets = exercise.setsPrescribed;
            if(entry.getValue().size() < sets) {
                currentExercise = entry.getKey();
                currentRestTime = exercise.restSeconds;
                return;
            }
        }
        // no suitable exercise found
        currentExercise = null;
    }

    private void setDataSetToCurrentExerciseSets() {
        /**
         * sets recyclerview adapter's data set to the sets for the
         * current exercise in the session
         */
        if(currentExercise!=null) {
            currentExerciseSets.clear();
            currentExerciseSets.addAll(exercisesWithSets.get(currentExercise));
            adapter.notifyDataSetChanged();
        }
    }

    private void paintTrainingSessionInfo(boolean updateCurrentExercise) {
        if(activeProgramId == 0L) {
            paintEmptyState(EmptyStates.NO_ACTIVE_PROGRAM);
        } else if (session == null) {
            paintEmptyState(EmptyStates.REST_DAY);
        } else {
            if(updateCurrentExercise) {
                updateCurrentExercise();
            }
            setDataSetToCurrentExerciseSets();
            if(currentExercise != null) {
                currentExerciseTextView
                        .setText(currentExercise.name);
            } else {
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
        } else {
            dispatchTakeVideoIntent();
        }
    }

    private void insertTrainingSet(short reps, double weight) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                TrainingSessionSet set = new TrainingSessionSet(
                        currentExercise.id,
                        session.id,
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
                //updateCurrentExercise();
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
                restTimerText.setVisibility(View.GONE);
                setControlsLayoutEnabled(true);
                updateCurrentExercise();
                setDataSetToCurrentExerciseSets();
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
        LinearLayout layout = getActivity().findViewById(R.id.training_session_controls_container);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(enabled);
        }
    }


    private void startNextSetTimer() {
       restTimerText.setVisibility(View.VISIBLE);
       setControlsLayoutEnabled(false);
        remainingRestTime = currentRestTime;
        restTimerHandler.postDelayed(this, 0);


        /*AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getContext(),TIMER_UP,alarmIntent,0)
        am.setAlarmClock(
                new AlarmManager.AlarmClockInfo(getAlarmTargetTime().toEpochMilli(), pi),
                pi
        );*/
    }
}