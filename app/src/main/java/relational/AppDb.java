package relational;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import relational.dao.ExerciseDao;
import relational.entities.Exercise;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingDayExerciseSet;
import relational.entities.TrainingProgram;

@Database(entities={
        Exercise.class,
        TrainingProgram.class,
        TrainingDay.class,
        TrainingDayExercise.class,
        TrainingDayExerciseSet.class
}, version=1)
public abstract class AppDb extends RoomDatabase {
    public abstract ExerciseDao exerciseDao();
}
