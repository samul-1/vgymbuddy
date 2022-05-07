package it.bsamu.sam.virtualgymbuddy.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import relational.entities.Exercise;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingSessionSet;

public class CurrentTrainingSessionViewModel extends ViewModel {
    private MutableLiveData<Map<Exercise, List<TrainingSessionSet>>> exercisesWithSets = new MutableLiveData<>();
    private MutableLiveData<Long> trainingDayId = new MutableLiveData<>(0L);
    private MutableLiveData<List<TrainingDayExercise>> trainingDayExercises = new MutableLiveData<>();
    private MutableLiveData<Long> sessionId = new MutableLiveData<>(0L);
    Exercise currentExercise;
    List<TrainingSessionSet> currentExerciseSets = new LinkedList<>();
    private MutableLiveData<Short> currentRestTime = new MutableLiveData<>((short)0);
    private MutableLiveData<Short> remainingRestTime = new MutableLiveData<>((short)0);
    public MutableLiveData<Long> activeProgramId;

    public void updateCurrentExercise() {
        /**
         * sets the current exercise instance variable to the first exercise for which
         * not all sets have been completed
         */
        System.out.println("updating exercise");
        if(exercisesWithSets.getValue() == null) {
            return;
        }
        for(Map.Entry<Exercise, List<TrainingSessionSet>>entry : exercisesWithSets.getValue().entrySet()) {
            TrainingDayExercise exercise = trainingDayExercises.getValue()
                    .stream()
                    .filter(e->e.exerciseId==entry.getKey().id)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("cannot find exercise " + entry.getKey()));
            long sets = exercise.setsPrescribed;
            if(entry.getValue().size() < sets) {
                currentExercise = entry.getKey();
                currentRestTime.postValue(exercise.restSeconds);
                return;
            }
        }
        // no suitable exercise found
        currentExercise = null;
    }

    public Exercise getCurrentExercise() {
        return currentExercise;
    }

    public void setDataSetToCurrentExerciseSets(RecyclerView.Adapter adapter) {
        /**
         * sets recyclerview adapter's data set to the sets for the
         * current exercise in the session
         */
        if(currentExercise!=null && exercisesWithSets.getValue()!=null) {
            currentExerciseSets.clear();
            currentExerciseSets.addAll(exercisesWithSets.getValue().get(currentExercise));
           adapter.notifyDataSetChanged();
        }
    }

    public MutableLiveData<Long> getSessionId() {
        return sessionId;
    }
    public MutableLiveData<Long> getActiveProgramId() {
        if(activeProgramId == null) {
          activeProgramId = new MutableLiveData<>(0L);
        }
        return activeProgramId;
    }
    public MutableLiveData<Short> getCurrentRestTime() {
        return currentRestTime;
    }
    public MutableLiveData<Short> getRemainingRestTime() {
        return remainingRestTime;
    }

    public MutableLiveData<Long> getTrainingDayId() {return trainingDayId;}

    public void setCurrentRestTime(short time) {
        currentRestTime.postValue(time);
    }
    public void setRemainingRestTime(short time) {
        remainingRestTime.postValue(time);
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
    public void setActiveProgramId(long id) {
        activeProgramId.setValue(id);
    }

}
