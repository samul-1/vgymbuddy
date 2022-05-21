package relational.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import relational.entities.GymTransition;

@Dao
public interface GymTransitionDao {

    @Insert
    public void insertGymTransition(GymTransition gymTransition);

    @Query("SELECT * FROM GymTransition " +
            "WHERE timestamp BETWEEN :beginTimestamp and :endTimestamp")
    public List<GymTransition> getTransitionsForTimePeriod(Date beginTimestamp, Date endTimestamp);
}
