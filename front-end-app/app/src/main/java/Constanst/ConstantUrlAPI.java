package Constanst;

public class ConstantUrlAPI {
    //    public final static String BASE_URL_API = "http://192.168.1.209:8099/api/v1/";
//    public final static String BASE_URL_API = "http://10.0.2.2:8099/api/v1/";
//    public final static String BASE_URL_API = "http://192.168.1.10:8099/api/v1/";
//    public final static String BASE_URL_API = "http://172.20.10.4:8099/api/v1/";
    public final static String BASE_URL_API = "http://192.168.1.9:8099/api/v1/";
//    public final static String BASE_URL_API = "http://192.168.1.20:8099/api/v1/";
    //public final static String BASE_URL_API = "http://192.168.1.31:8099/api/v1/";
//    public final static String BASE_URL_API = "http://192.168.1.31:8099/api/v1/";
//    public final static String BASE_URL_API = "http://192.168.167.36:8099/api/v1/";
//    public final static String BASE_URL_API = "http://192.168.230.70:8099/api/v1/";
    public final static String URL_GET_USER_BY_ID = BASE_URL_API + "auth/user/";
    public final static String URL_SIGN_IN = BASE_URL_API + "auth/sign-in";
    public final static String URL_SIGN_UP = BASE_URL_API + "auth/sign-up";
    public final static String URL_GETDATA_HOME = BASE_URL_API + "song/home";
    public final static String URL_SEARCH_SONG = BASE_URL_API + "song/search?";
    public final static String URL_GET_USER_HISTORY = BASE_URL_API + "history/";
    public final static String URL_ADD_USER_HISTORY = BASE_URL_API + "history";
    public final static String URL_REMOVE_HISTORY = BASE_URL_API + "history/remove";
    public final static String URL_REMOVE_ALL_HISTORY = BASE_URL_API + "history/remove-all";
    public final static String URL_GET_LIKED_BY_USERID = BASE_URL_API + "song/liked/";
    public final static String URL_RESET_PASSWORD = BASE_URL_API + "auth/reset-password";
    public final static String URL_GET_ALL_COMMENT_BY_SONG = BASE_URL_API + "song/comment/all/";
    public final static String URL_REMOVE_COMMENT_BY_ID = BASE_URL_API + "song/comment/remove/";
    public final static String URL_EDIT_COMMENT_BY_USER = BASE_URL_API + "song/comment/edit";
    public final static String URL_ADD_COMMENT_BY_USER = BASE_URL_API + "song/comment/add";

    // playlist

    public final static String URL_ADD_PLAYLIST_USER = BASE_URL_API + "playlist/add";
    public final static String URL_REMOVE_PLAYLIST = BASE_URL_API + "playlist/remove/";
    public final static String URL_REMOVE_SONG_PLAYLIST = BASE_URL_API + "playlist/remove/song/";
    public final static String URL_ADD_SONG_TO_PLAYLIST = BASE_URL_API + "playlist/add/song";
    public final static String URL_GET_ALL_PLAYLIST_BY_USER = BASE_URL_API + "playlist/all/";
    public final static String URL_GET_ALL_PLAYLIST_BY_USER_NOT_EXIST_SONG = BASE_URL_API + "playlist/all/not-exist-song/";
    public final static String URL_GET_DETAIL_PLAYLIST = BASE_URL_API + "playlist/detail/";

    // follow
    public final static String URL_FOLLOW = BASE_URL_API + "follow";
    public final static String URL_UN_FOLLOW = BASE_URL_API + "follow/un-follow";
    public final static String URL_GET_ALL_FOLLOWER = BASE_URL_API + "follow/follower/";
    public final static String URL_GET_ALL_FOLLOWEE = BASE_URL_API + "follow/followee/";
    public final static String URL_CHECK_FOLLOW_EXIST = BASE_URL_API + "follow/check-exist-follow";

    // song user
    public final static String URL_GET_PUBLIC_SONG_BY_USER = BASE_URL_API + "song/public/";
    public final static String URL_GET_PRIVATE_SONG_BY_USER = BASE_URL_API +  "song/private/";
    public final static String URL_GET_SONG_BY_USER = BASE_URL_API + "song/all/";
    public final static String URL_ADD_SONG = BASE_URL_API + "song";
    public final static String URL_REMOVE_SONG = BASE_URL_API + "song/";
    public final static String URL_CHANGE_SCOPE_SONG = BASE_URL_API + "song/change-scope/";

    // playlist user
    public final static String URL_GET_PUBLIC_PLAYLIST_BY_USER = BASE_URL_API + "playlist/all/public/";
    public final static String URL_GET_PLAYLIST_BY_USER = BASE_URL_API +  "playlist/all/";
    public final static String URL_GET_PRIVATE_PLAYLIST_BY_USER = BASE_URL_API + "playlist/all/private/";

    // like song
    public final static String URL_LIKE = BASE_URL_API + "song/like";
    public final static String URL_UN_LIKE = BASE_URL_API +  "song/un-like";
    public final static String URL_IS_CHECK_USERLIKE_SONG = BASE_URL_API + "song/is-check-user-like";

    // change pasword
    public final static String URL_CHANGE_PASSWORD_BYUSER = BASE_URL_API + "auth/change-password";

    // profile
    public final static String URL_GET_PROFILE_BY_USER = BASE_URL_API + "user/profile/";
    public final static String URL_EDIT_PROFILE_BY_USER = BASE_URL_API + "user/profile/edit";

}
