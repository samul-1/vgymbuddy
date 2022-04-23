package relational.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"exerciseId", "trainingSessionId"},
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
    public long exerciseId;
    public long trainingSessionId;

    public double weightUsed;
    public short repsDone;
    public String notes;
}
