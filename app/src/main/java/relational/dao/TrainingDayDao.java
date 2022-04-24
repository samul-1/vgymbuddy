package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingProgram;

@Dao
public interface TrainingDayDao {
    @Query("SELECT * FROM TrainingDay")
    Cursor getAll();

    @Query("SELECT * FROM trainingday WHERE _id = :id")
    TrainingDay getById(long id);

    @Query("SELECT * FROM TrainingDay WHERE trainingProgramId = :programId ORDER BY dayOfWeek")
    Cursor getForProgram(long programId);

    @Query("SELECT * FROM TrainingDay WHERE trainingProgramId = :programId AND dayOfWeek = :dayOfWeek")
    TrainingDay getForProgramAndDayOfWeek(long programId, short dayOfWeek);

    @Query("SELECT * FROM TrainingDayExercise WHERE trainingDayId = :trainingDayId")
    List<TrainingDayExercise> getExercisesFor(long trainingDayId);

    @Insert
    long insertTrainingDay(TrainingDay day);

}
