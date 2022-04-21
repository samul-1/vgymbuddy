package relational.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"exerciseId", "trainingDayId"},
        foreignKeys = {
                @ForeignKey(
                        entity = TrainingDay.class,
                        parentColumns = "_id",
                        childColumns = "trainingDayId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "_id",
                        childColumns = "exerciseId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class TrainingDayExercise {
    public long exerciseId;
    public long trainingDayId;

    public short setsPrescribed;
    public short repsPrescribed;

    public TrainingDayExercise(long eId, long tId, short sets, short reps) {
        this.exerciseId = eId;
        this.trainingDayId = tId;
        this.repsPrescribed = reps;
        this.setsPrescribed = sets;
    }
}
