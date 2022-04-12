package relational.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TrainingDay {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long trainingProgramId; // fk to TrainingProgram
    public short position;
}
