package Interface;

import Model.CurrentPlaylist;
import Model.Song;

public interface BottomSheetPlayMusicInteractionListener {
    void onCurrentSongChange(Song song);
    void onCurrentListChange(CurrentPlaylist currentPlaylist);
}
