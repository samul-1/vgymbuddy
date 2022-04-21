package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import relational.entities.TrainingProgram;

@Dao
public interface TrainingProgramDao {
    @Query("SELECT * FROM TrainingProgram")
    Cursor getAll();

    @Query("SELECT * FROM TrainingProgram WHERE _id = :id")
    TrainingProgram getById(long id);

    @Insert
    public long insertTrainingProgram(TrainingProgram program);
}
