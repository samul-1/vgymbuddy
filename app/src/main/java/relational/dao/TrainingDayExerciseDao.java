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
            "SELECT repsPrescribed, setsPrescribed, name, trainingDayId " +
                    "FROM TrainingDayExercise INNER JOIN Exercise ON exerciseId = Exercise._id " +
                    "WHERE trainingDayId = :dayId"
    )
    Cursor getExercisesForTrainingDay(long dayId);


}
