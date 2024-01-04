package Interface;

import Model.User;

public interface BottomSheetActionArtistInteractionListener {
    void onChangeUnFollow(User user);
    void onChangeFollow(User user);
}
