package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import relational.entities.Exercise;

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    Cursor getAll();

    @Insert
    public long insertExercise(Exercise exercise);
}
