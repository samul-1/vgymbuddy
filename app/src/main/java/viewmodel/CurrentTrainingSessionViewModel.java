package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingSession;
import relational.entities.TrainingSessionSet;

public class CurrentTrainingSessionViewModel extends ViewModel {
    private MutableLiveData<Map<Exercise, List<TrainingSessionSet>>> exercisesWithSets = new MutableLiveData<>();
    private MutableLiveData<Long> trainingDayId = new MutableLiveData<>();
    private MutableLiveData<List<TrainingDayExercise>> trainingDayExercises = new MutableLiveData<>();
    private MutableLiveData<Long> sessionId = new MutableLiveData<>();
    Exercise currentExercise;
    List<TrainingSessionSet> currentExerciseSets = new LinkedList<>();
    short currentRestTime;

    public void updateCurrentExercise() {
        /**
         * sets the current exercise instance variable to the first exercise for which
         * not all sets have been completed
         */
        System.out.println("updating exercise");
        for(Map.Entry<Exercise, List<TrainingSessionSet>>entry : exercisesWithSets.getValue().entrySet()) {
            TrainingDayExercise exercise = trainingDayExercises.getValue()
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

    public void setDataSetToCurrentExerciseSets() {
        /**
         * sets recyclerview adapter's data set to the sets for the
         * current exercise in the session
         */
        if(currentExercise!=null) {
            currentExerciseSets.clear();
            currentExerciseSets.addAll(exercisesWithSets.getValue().get(currentExercise));
           // adapter.notifyDataSetChanged();
        }
    }

    public long getSessionId() {
        return sessionId.getValue();
    }

    public void setTrainingDayId(long id) {
        trainingDayId.postValue(id);
    }

    public void setSessionId(long id) {
        sessionId.postValue(id);
    }

    public void setTrainingDayExercises(List<TrainingDayExercise> sets) {
        trainingDayExercises.postValue(sets);
    }

    public void setExercisesWithSets(Map<Exercise, List<TrainingSessionSet>> sets) {
        exercisesWithSets.postValue(sets);
    }

}
