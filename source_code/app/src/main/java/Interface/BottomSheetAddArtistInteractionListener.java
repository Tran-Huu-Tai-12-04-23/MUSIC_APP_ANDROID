package Interface;

import Model.User;

public interface BottomSheetAddArtistInteractionListener {
    void onChangeUnFollow(User user);
    void onChangeFollow(User user);
}
