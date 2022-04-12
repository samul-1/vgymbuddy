package relational.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TrainingProgram {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String description;
}
