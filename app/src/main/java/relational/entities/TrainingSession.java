package relational.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = TrainingDay.class,
                        parentColumns = "_id",
                        childColumns = "trainingDayId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class TrainingSession {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    public long trainingDayId;
    public Date timestamp;

    public TrainingSession(long trainingDayId, Date timestamp) {
        this.trainingDayId = trainingDayId;
        this.timestamp = timestamp;
    }
}
