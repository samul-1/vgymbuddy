package relational.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import relational.entities.Exercise;

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    List<Exercise> getAll();

    @Insert
    public void insertExercise(Exercise exercise);
}
