package LocalData.DTO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import LocalData.Entity.StatePlayMusic;

@Dao
public interface StatePlayMusicDao {
    @Insert
    void insert(StatePlayMusic statePlayMusic);

    @Update
    void update(StatePlayMusic statePlayMusic);

    @Query("SELECT * FROM state_play_music WHERE id = :id")
    StatePlayMusic getStatePlayMusicById(int id);

}