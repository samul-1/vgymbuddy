package relational.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"exerciseId", "trainingDayId"})
public class TrainingDayExerciseSet {
    public long exerciseId;
    public long trainingDayId;

    public double weightUsed;
    public short repsDone;
    public String notes;
}
