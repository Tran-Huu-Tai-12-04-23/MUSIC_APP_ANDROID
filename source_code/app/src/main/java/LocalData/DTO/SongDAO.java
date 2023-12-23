package LocalData.DTO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import LocalData.Entity.SongEntity;

@Dao
public interface SongDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(SongEntity song);

    @Update
    void updateSong(SongEntity song);

    @Delete
    void deleteSong(SongEntity song);

    @Query("SELECT * FROM songs WHERE id = :songId")
    SongEntity getSongById(long songId);

    @Query("SELECT * FROM songs")
    List<SongEntity> getAllSongs();
}
