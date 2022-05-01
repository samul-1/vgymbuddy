package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import relational.entities.Exercise;
import relational.entities.TrainingSessionSet;
import relational.entities.TrainingSession;

@Dao
public interface TrainingSessionSetDao {
    @Insert
    long insertSet(TrainingSessionSet set);

    @Query("SELECT repsDone, weightUsed, videoUri, timestamp  FROM TrainingSessionSet " +
            "INNER JOIN TrainingSession " +
            "ON trainingSessionId = _id WHERE exerciseId = :exerciseId AND videoUri <> \"\"")
    Cursor getForExerciseWithVideo(long exerciseId);

    @Query(
        "SELECT * FROM Exercise LEFT OUTER JOIN TrainingSessionSet " +
        "ON (Exercise._id = TrainingSessionSet.exerciseId AND TrainingSessionSet.trainingSessionId = :sessionId) " +
        "WHERE " +
        "Exercise._id IN (" +
        "   SELECT _id FROM Exercise INNER JOIN " +
        "   TrainingDayExercise ON _id = exerciseId WHERE " +
        "   TrainingDayExercise.trainingDayId = (" +
        "       SELECT trainingDayId FROM TrainingSession " +
        "       WHERE _id = :sessionId" +
        "   )" +
        ")"

    )
    Map<Exercise, List<TrainingSessionSet>> getExercisesWithSetsForSession(long sessionId);

   /* default Map<Exercise, List<TrainingSessionSet>> getExercisesWithSetsForSession(long sessionId) {
        Map<Exercise, List<TrainingSessionSet>> m = _getExercisesWithSetsForSession(sessionId);
        Map<Exercise, List<TrainingSessionSet>> ret = new HashMap<>();

        for (Map.Entry<Exercise, List<TrainingSessionSet>> entry : m.entrySet()) {
            if (ret.get(entry.getKey())==null) {
                ret.put(entry.getKey(), new LinkedList<>());
            }
            ret.get(entry.getKey()).addAll(entry.getValue());
        }
        return ret;
    }*/
 }
