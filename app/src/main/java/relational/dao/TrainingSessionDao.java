package relational.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import relational.entities.TrainingSession;

@Dao
public interface TrainingSessionDao {

    @Insert
    public long insertTrainingSession(TrainingSession session);

    @Query("SELECT * FROM TrainingSession WHERE _id = :id")
    public TrainingSession getById(long id);

    @Query("SELECT * FROM TrainingSession WHERE trainingDayId = :trainingDayId AND " +
            "CAST(timestamp AS DATE) = CAST(DATE('now') AS DATE)")
    public TrainingSession getTodaysTrainingSessionForTrainingDay(long trainingDayId);

}
