package relational;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import relational.dao.ExerciseDao;
import relational.dao.TrainingDayDao;
import relational.dao.TrainingProgramDao;
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
    public abstract TrainingProgramDao trainingProgramDao();
    public abstract TrainingDayDao trainingDayDao();

    private static AppDb instance = null;

    public static synchronized AppDb getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                context,
                AppDb.class,
                "db"
            ).build();
        }
        return instance;
    }
}
