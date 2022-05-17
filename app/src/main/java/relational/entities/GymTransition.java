package relational.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class GymTransition {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    public Date timestamp;
    public boolean entering;

    public GymTransition(Date timestamp, boolean entering) {
        this.timestamp = timestamp;
        this.entering = entering;
    }
}
