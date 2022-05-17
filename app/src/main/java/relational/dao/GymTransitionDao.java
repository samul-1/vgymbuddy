package relational.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import relational.entities.GymTransition;

@Dao
public interface GymTransitionDao {

    @Insert
    public void insertGymTransition(GymTransition gymTransition);

    // TODO implement query to get all transitions from last week
    // make a comparison between the time spent at the gym according to transitions,
    // and according to session's data (begin and end timestamp), plot onto a chart and show in a new fragment
}
