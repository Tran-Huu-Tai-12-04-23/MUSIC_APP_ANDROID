package LocalData.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import Constanst.Constant;

@Entity(tableName = "state_play_music")
public class StatePlayMusic implements Serializable {
    @PrimaryKey
    private int id = Constant.ID_STATE_MUSIC;
    private boolean isPlaying;
    private boolean isPause;
    private boolean isRepeat;
    private boolean isShuffle;
    private int seekToValue;

    // Các phương thức getter và setter ở đây

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    public int getSeekToValue() {
        return seekToValue;
    }

    public void setSeekToValue(int seekToValue) {
        this.seekToValue = seekToValue;
    }

    @Override
    public String toString() {
        return "StatePlayMusic{" +
                "id=" + id +
                ", isPlaying=" + isPlaying +
                ", isPause=" + isPause +
                ", isRepeat=" + isRepeat +
                ", isShuffle=" + isShuffle +
                ", seekToValue=" + seekToValue +
                '}';
    }
}
