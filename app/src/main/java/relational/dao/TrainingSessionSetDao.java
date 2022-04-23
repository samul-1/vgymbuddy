package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;
import java.util.Map;

import relational.entities.Exercise;
import relational.entities.TrainingSessionSet;
import relational.entities.TrainingSession;

@Dao
public interface TrainingSessionSetDao {
    @Query(
        "SELECT * FROM Exercise LEFT OUTER JOIN TrainingSessionSet " +
        "ON Exercise._id = TrainingSessionSet.exerciseId WHERE " +
        "TrainingSessionSet.trainingSessionId = :sessionId AND " +
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
}
