package relational.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = TrainingProgram.class,
                parentColumns = "_id",
                childColumns = "trainingProgramId",
        onDelete = ForeignKey.CASCADE
        )
})
public class TrainingDay {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    public long trainingProgramId;
    public short position;

    public TrainingDay(long trainingProgramId, short position) {
        this.trainingProgramId = trainingProgramId;
        this.position = position;
    }

    public static class TrainingDayExerciseData {
        @Embedded
        public Exercise exercise;

    }
}
