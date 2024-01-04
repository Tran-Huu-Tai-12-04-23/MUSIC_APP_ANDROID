package LocalData.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import Model.Song;

@Entity(tableName = "playing_list",
        foreignKeys = {
                @ForeignKey(entity = CurrentPlaylistEntity.class, parentColumns = "id", childColumns = "currentPlaylistId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Song.class, parentColumns = "id", childColumns = "songId", onDelete = ForeignKey.CASCADE)
        })
public class PlayingListEntity {
    @PrimaryKey
    private long currentPlaylistId;
    private long songId;

    public long getCurrentPlaylistId() {
        return currentPlaylistId;
    }

    public void setCurrentPlaylistId(long currentPlaylistId) {
        this.currentPlaylistId = currentPlaylistId;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
