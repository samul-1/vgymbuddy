package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import relational.entities.TrainingDay;
import relational.entities.TrainingProgram;

@Dao
public interface TrainingDayDao {
    @Query("SELECT * FROM TrainingDay")
    Cursor getAll();

    @Query("SELECT * FROM trainingday WHERE _id = :id")
    TrainingDay getById(long id);

    @Query("SELECT * FROM TrainingDay WHERE trainingProgramId = :programId ORDER BY position")
    Cursor getForProgram(long programId);

    @Query("SELECT MAX(position) FROM TrainingDay WHERE trainingProgramId = :programId")
    short getHighestPositionForProgram(long programId);


    @Insert
    long insertTrainingDay(TrainingDay day);

}
