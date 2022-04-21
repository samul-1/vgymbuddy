package relational.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TrainingProgram {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    public String name;
    public String description;

    public TrainingProgram(String name, String description) {
        this.name = name;
        this.description= description;
    }
}
