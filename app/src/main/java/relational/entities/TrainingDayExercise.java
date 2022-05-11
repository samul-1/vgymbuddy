package relational.entities;

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
    public short restSeconds;

    public TrainingDayExercise(long exerciseId, long trainingDayId, short setsPrescribed, short repsPrescribed, short restSeconds) {
        this.exerciseId = exerciseId;
        this.trainingDayId = trainingDayId;
        this.repsPrescribed = repsPrescribed;
        this.setsPrescribed = setsPrescribed;
        this.restSeconds = restSeconds;
    }

   /* public TrainingDayExercise(long exerciseId, long trainingDayId, short setsPrescribed, short repsPrescribed, short restSeconds) {
        this.exerciseId = exerciseId;
        this.trainingDayId = trainingDayId;
        this.repsPrescribed = repsPrescribed;
        this.setsPrescribed = setsPrescribed;
        this.restSeconds = restSeconds;
    }*/
}
