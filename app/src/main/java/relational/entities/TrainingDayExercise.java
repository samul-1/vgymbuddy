package relational.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"exerciseId", "trainingDayId"})
public class TrainingDayExercise {
    public long exerciseId;
    public long trainingDayId;

    public short setsPrescribed;
    public short repsPrescribed;
    public String notes;
}
