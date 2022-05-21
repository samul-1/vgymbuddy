package relational.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import relational.entities.TrainingSession;

@Dao
public interface TrainingSessionDao {

    @Insert
    public long insertTrainingSession(TrainingSession session);

    @Query("SELECT * FROM TrainingSession WHERE _id = :id")
    public TrainingSession getById(long id);

    @Query("SELECT * FROM TrainingSession WHERE trainingDayId = :trainingDayId AND " +
            "beginTimestamp BETWEEN :minTimestamp AND :maxTimestamp")
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

    // returns the timestamp of the last set done for a training session
    @Query("SELECT MAX(timestamp) FROM TrainingSessionSet " +
            "WHERE trainingSessionId = :trainingSessionId")
    public Date getTrainingSessionEndTimestamp(long trainingSessionId);

    // returns the begin timestamp, end timestamp, and day of week of all training
    // session whose beginTimestamp is in the specified interval
    @Query("SELECT beginTimestamp, dayOfWeek, MAX(timestamp) AS endTimestamp " +
            "FROM TrainingSession INNER JOIN TrainingSessionSet ON " +
            "TrainingSession._id = trainingSessionId " +
            "INNER JOIN TrainingDay ON TrainingDay._id = trainingDayId " +
            "WHERE beginTimestamp BETWEEN :beginDate AND :endDate")
    public List<TrainingSessionDurationData> getDurationDataForTimeInterval(Date beginDate, Date endDate);

    @Update
    public void updateTrainingSessions(TrainingSession ...sessions);

    public class TrainingSessionDurationData {
        public Date beginTimestamp;
        public Date endTimestamp;
        public short dayOfWeek;

        public long getDurationInMinutes() {
            return TimeUnit.MINUTES.convert(
                    endTimestamp.getTime() - beginTimestamp.getTime(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

}
