package relational;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import relational.dao.ExerciseDao;
import relational.dao.GymTransitionDao;
import relational.dao.TrainingDayDao;
import relational.dao.TrainingDayExerciseDao;
import relational.dao.TrainingProgramDao;
import relational.dao.TrainingSessionDao;
import relational.dao.TrainingSessionSetDao;
import relational.entities.Exercise;
import relational.entities.GymTransition;
import relational.entities.TrainingDay;
import relational.entities.TrainingDayExercise;
import relational.entities.TrainingSession;
import relational.entities.TrainingSessionSet;
import relational.entities.TrainingProgram;

@Database(entities={
        Exercise.class,
        TrainingProgram.class,
        TrainingDay.class,
        TrainingDayExercise.class,
        TrainingSessionSet.class,
        TrainingSession.class,
        GymTransition.class
}, version=1)
@TypeConverters({Converters.class})
public abstract class AppDb extends RoomDatabase {
    public abstract ExerciseDao exerciseDao();
    public abstract TrainingProgramDao trainingProgramDao();
    public abstract TrainingDayDao trainingDayDao();
    public abstract TrainingDayExerciseDao trainingDayExerciseDao();
    public abstract TrainingSessionSetDao trainingSessionSetDao();
    public abstract TrainingSessionDao trainingSessionDao();
    public abstract GymTransitionDao gymTransitionDao();

    private static AppDb instance = null;

    public static AppDb getInstance(Context context) {
        if(instance != null) {
            return instance;
        }
        synchronized (AppDb.class) {
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
}
