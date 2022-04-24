package relational.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = TrainingSession.class,
                        parentColumns = "_id",
                        childColumns = "trainingSessionId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "_id",
                        childColumns = "exerciseId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)public class TrainingSessionSet {
    @PrimaryKey(autoGenerate = true)
    public long sid;

    public long exerciseId;
    public long trainingSessionId;

    public double weightUsed;
    public short repsDone;
    public String notes;

    public TrainingSessionSet(long exerciseId, long trainingSessionId, short repsDone, double weightUsed) {
        this.exerciseId=exerciseId;
        this.trainingSessionId=trainingSessionId;
        this.repsDone=repsDone;
        this.weightUsed=weightUsed;
    }
}
