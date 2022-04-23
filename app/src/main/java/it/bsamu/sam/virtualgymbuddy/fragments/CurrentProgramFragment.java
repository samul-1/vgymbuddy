package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import adapter.TrainingSessionSetAdapter;
import it.bsamu.sam.virtualgymbuddy.R;
import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingSession;
import relational.entities.TrainingSessionSet;

public class CurrentProgramFragment extends AbstractCursorRecyclerViewFragment<TrainingSessionSetAdapter> {
    short todayWeekDayIdx;
    long activeProgramId;
    Map<Exercise, List<TrainingSessionSet>> exercisesWithSets;
    TrainingDay trainingDay;
    List<TrainingDayExercise> trainingDayExercises;
    TrainingSession session;
    Exercise currentExercise;
    List<TrainingSessionSet> currentExerciseSets = null;

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
                paintTrainingSessionInfo();
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
        System.out.println("with sets "+ exercisesWithSets.keySet());
        System.out.println("with sets "+ exercisesWithSets.values());
    }

    private synchronized void fetchOrCreateTodaysTrainingSession() {
        // attempts to retrieve a training session that matches the current
        // training day; if it doesn't exist, it creates one in the database
        session = db.trainingSessionDao().getTodaysTrainingSessionForTrainingDay(trainingDay.id);
        if(session == null) {
            long sessionId = db.trainingSessionDao().insertTrainingSession(new TrainingSession(
                    trainingDay.id, new Date()
            ));
            session = db.trainingSessionDao().getById(sessionId);
        }
        System.out.println("SESSION " + session);
    }

    private void fetchTrainingDay() {
        trainingDay = db.trainingDayDao().getForProgramAndDayOfWeek(activeProgramId, todayWeekDayIdx);
        System.out.println("fetching day for " + activeProgramId + ", " + todayWeekDayIdx + " fetched " + trainingDay);

        if(trainingDay != null) {
            trainingDayExercises = db.trainingDayDao().getExercisesFor(trainingDay.id);
            System.out.println("exercises " + trainingDayExercises);
        }
    }

    enum EmptyStates {
        NO_ACTIVE_PROGRAM,
        REST_DAY
    }

    private void paintEmptyState(EmptyStates state) {
        View emptyStateView = getActivity().findViewById(R.id.training_session_empty_state_container);
        // TODO this gets called even if not in this fragment (move to onResume etc.)
        emptyStateView.setVisibility(View.VISIBLE);

        ImageView iv = (ImageView) getActivity().findViewById(R.id.training_session_empty_state_icon);
        iv.setImageResource(state==EmptyStates.NO_ACTIVE_PROGRAM
                ? R.drawable.ic_baseline_sentiment_very_dissatisfied_24
                : R.drawable.ic_baseline_single_bed_24);

        TextView tv = (TextView) getActivity().findViewById(R.id.training_session_empty_state_desc);
        tv.setText(state==EmptyStates.NO_ACTIVE_PROGRAM
                ? R.string.no_active_program
                : R.string.rest_day);
    }

    private void setDataSetToCurrentExerciseSets() {
        /**
         * sets recyclerview adapter's data set to the sets for the
         * current exercise in the session
         */

        // find the first exercise for which not all sets have been logged
        for(Map.Entry<Exercise, List<TrainingSessionSet>>entry : exercisesWithSets.entrySet()) {
            TrainingDayExercise exercise = trainingDayExercises
                    .stream()
                    .filter(e->e.exerciseId==entry.getKey().id)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("cannot find exercise " + entry.getKey()));
            long sets = exercise.setsPrescribed;
            if(entry.getValue().size() < sets) {
                currentExercise = entry.getKey();
                currentExerciseSets = entry.getValue();
                break;
            }
        }
        System.out.println("Current exercise " + currentExercise + " sets " + currentExerciseSets);
        // TODO pass info to adapter to create viewholder
        adapter.notifyDataSetChanged();
    }

    private void paintTrainingSessionInfo() {
        if(activeProgramId == 0L) {
            paintEmptyState(EmptyStates.NO_ACTIVE_PROGRAM);
        } else if (session == null) {
            paintEmptyState(EmptyStates.REST_DAY);
        } else {
            setDataSetToCurrentExerciseSets();
        }
    }

}