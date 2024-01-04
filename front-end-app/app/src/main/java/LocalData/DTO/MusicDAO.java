package LocalData.DTO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import LocalData.Entity.CurrentPlaylistEntity;
import LocalData.Entity.PlayingListEntity;
import LocalData.Entity.SongEntity;


@Dao
public interface MusicDAO {
    @Insert
    long insertCurrentPlaylist(CurrentPlaylistEntity currentPlaylistEntity);

    @Update
    void updateCurrentPlaylist(CurrentPlaylistEntity currentPlaylistEntity);

    @Query("SELECT * FROM current_playlist WHERE id = :playlistId")
    LiveData<CurrentPlaylistEntity> getCurrentPlaylist(long playlistId);

    @Update
    void changeCurrentSong(SongEntity newCurrentSong);

    @Query("SELECT * FROM playing_list, songs WHERE currentPlaylistId = :playlistId and songs.id == playing_list.songId ")
    List<SongEntity> getSongsForPlayinglist(long playlistId);

    @Insert
    long addSongIntoPlayingList(PlayingListEntity playingListEntity);

    @Delete
    boolean deleteSongFromPlayingList(long playingListId);
}

