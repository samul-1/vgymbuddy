package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import relational.entities.TrainingDayExercise;

@Dao
public interface TrainingDayExerciseDao {
    @Insert
    long insertTrainingDayExercise(TrainingDayExercise trainingDayExercise);

    @Query(
            "SELECT repsPrescribed, setsPrescribed, name " +
                    "FROM TrainingDayExercise INNER JOIN Exercise ON exerciseId = _id " +
                    "WHERE trainingDayId = :dayId"
    )
    Cursor getExercisesForTrainingDay(long dayId);
}
