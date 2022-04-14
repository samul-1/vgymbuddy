package relational.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    public String name;
    public String description;

    public Exercise() {
    }
    public Exercise(String name) {
        this.name=name;
    }
}
