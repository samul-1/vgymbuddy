package relational.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import relational.entities.TrainingSession;

@Dao
public interface TrainingSessionDao {

    @Insert
    public long insertTrainingSession(TrainingSession session);

    @Query("SELECT * FROM TrainingSession WHERE _id = :id")
    public TrainingSession getById(long id);

    @Query("SELECT * FROM TrainingSession WHERE trainingDayId = :trainingDayId AND " +
            "timestamp BETWEEN :minTimestamp AND :maxTimestamp")
    public TrainingSession getTrainingSessionBetweenDatesForTrainingDay(Date minTimestamp, Date maxTimestamp, long trainingDayId);


    public default TrainingSession getTodaysTrainingSessionForTrainingDay(long trainingDayId) {
        LocalDateTime now = LocalDateTime.now(); // current date and time
        LocalDateTime midnight = now.toLocalDate().atStartOfDay();
        LocalDateTime _2359 = midnight.plusHours(23).plusMinutes(59).plusSeconds(59);
        Date midnightToday = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
        Date endOfToday = Date.from(_2359.atZone(ZoneId.systemDefault()).toInstant());

        return getTrainingSessionBetweenDatesForTrainingDay(
                midnightToday,
                endOfToday,
                trainingDayId
        );
    }

}
