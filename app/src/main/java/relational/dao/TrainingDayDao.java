package relational.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface TrainingDayDao {
    @Query("SELECT * FROM TrainingDay")
    Cursor getAll();
}
