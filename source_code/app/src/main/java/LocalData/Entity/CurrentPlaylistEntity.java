package LocalData.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "current_playlist")
public class CurrentPlaylistEntity {
    @PrimaryKey
    private long id;
    private long currentSongId;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCurrentSongId() {
        return currentSongId;
    }

    public void setCurrentSongId(long currentSongId) {
        this.currentSongId = currentSongId;
    }
}
