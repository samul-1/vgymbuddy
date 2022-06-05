package it.bsamu.sam.virtualgymbuddy.viewmodel;

import androidx.lifecycle.LiveData;
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
    private MutableLiveData<Exercise> currentExercise = new MutableLiveData<>();
    private MutableLiveData<List<TrainingSessionSet>> currentExerciseSets;
    private MutableLiveData<Short> currentRestTime = new MutableLiveData<>((short)0);
    private MutableLiveData<Short> remainingRestTime = new MutableLiveData<>((short)0);
    private MutableLiveData<Long> activeProgramId;
    private MutableLiveData<Boolean> addSetBtnActive = new MutableLiveData<>(false);

    public void updateCurrentExercise() {
        /**
         * sets the current exercise instance variable to the first exercise for which
         * not all sets have been completed
         */
        if(exercisesWithSets.getValue() == null) {
            return;
        }
        for(Map.Entry<Exercise, List<TrainingSessionSet>>entry : exercisesWithSets.getValue().entrySet()) {
            TrainingDayExercise exercise = trainingDayExercises.getValue()
                    .stream()
                    .filter(e -> e.exerciseId == entry.getKey().id)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("cannot find exercise " + entry.getKey()));
            long sets = exercise.setsPrescribed;
            if(entry.getValue().size() < sets) {
                // fewer sets than the prescribed amount exist for this exercise: this
                // is the current exercise (i.e. the latest one the user has gotten to)
                currentExercise.setValue(entry.getKey());
                currentRestTime.setValue(exercise.restSeconds);
                setDataSetToCurrentExerciseSets();
                return;
            }
        }
        // no suitable exercise found
        currentExercise.setValue(null);
        setDataSetToCurrentExerciseSets();
    }

    public void addCurrentExerciseSet(TrainingSessionSet set) {
       exercisesWithSets.getValue().get(currentExercise.getValue()).add(set);
        // trigger observers
        currentExerciseSets
                .postValue(
                        exercisesWithSets.getValue().get(
                                currentExercise.getValue()
                        )
                );
    }

    public void setDataSetToCurrentExerciseSets() {
        /**
         * sets recyclerview adapter's data set to the sets for the
         * current exercise in the session
         */
        if(currentExercise.getValue() != null && exercisesWithSets.getValue() != null) {
            currentExerciseSets
                    .postValue(
                            exercisesWithSets.getValue().get(currentExercise.getValue())
                    );
        }
    }

    public LiveData<Exercise> getCurrentExercise() {
        return currentExercise;
    }
    public LiveData<Long> getSessionId() {
        return sessionId;
    }
    public LiveData<Long> getActiveProgramId() {
        if(activeProgramId == null) {
          activeProgramId = new MutableLiveData<>(0L);
        }
        return activeProgramId;
    }
    public LiveData<Short> getCurrentRestTime() {
        return currentRestTime;
    }
    public LiveData<Short> getRemainingRestTime() {
        return remainingRestTime;
    }
    public LiveData<List<TrainingSessionSet>> getCurrentExerciseSets() {
        if(currentExerciseSets == null) {
            currentExerciseSets = new MutableLiveData<>(new LinkedList<>());
        }
        return currentExerciseSets;
    }
    public LiveData<Long> getTrainingDayId() {
        return trainingDayId;
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

    public LiveData<Boolean> getAddSetBtnEnabled() {
        return addSetBtnActive;
    }

    public void setAddSetBtnEnabled(boolean b) {
        addSetBtnActive.setValue(b);
    }
}
