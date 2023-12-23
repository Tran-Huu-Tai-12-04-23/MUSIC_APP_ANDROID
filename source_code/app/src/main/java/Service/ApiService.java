package Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import Constanst.ConstantUrlAPI;
import DTO.DetailPlaylistRequest;
import Model.Comment;
import Model.DetailPlaylist;
import Model.Follow;
import Model.Playlist;
import Model.Song;
import Model.User;
import DTO.ChangePasswordRequest;
import DTO.HomeResponse;
import DTO.ResponseData;
import Service.ModelRequest.HistoryRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            .create();
    ApiService ApiService = new Retrofit.Builder()
            .baseUrl(ConstantUrlAPI.BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Service.ApiService.class);


    @GET(ConstantUrlAPI.URL_GET_USER_BY_ID + "{userId}")
    Call<User>  getUserById(@Path("userId") Long userId);

    @GET(ConstantUrlAPI.URL_GETDATA_HOME)
    Call<HomeResponse> getHomePage();

    @POST(ConstantUrlAPI.URL_SIGN_IN)
    Call<ResponseData<User>> login(@Body User user);

    @POST(ConstantUrlAPI.URL_SIGN_UP)
    Call<ResponseData<User>> register(@Body User user);

    @POST(ConstantUrlAPI.URL_RESET_PASSWORD)
    Call<ResponseData<User>> resetPassword(@Body ChangePasswordRequest changePasswordRequest);

    @GET(ConstantUrlAPI.URL_SEARCH_SONG)
    Call<List<Song>> searchSong(@Query("page") int page, @Query("key") String key);

    @GET(ConstantUrlAPI.URL_GET_USER_HISTORY+"{userId}")
    Call<ResponseData<List<Song>>>  getUserHistory(@Path("userId") Long userId);

    @GET(ConstantUrlAPI.URL_GET_LIKED_BY_USERID+"{userId}")
    Call<ResponseData<List<Song>>>  getSongLiked(@Path("userId") Long userId);

    @POST(ConstantUrlAPI.URL_ADD_USER_HISTORY)
    Call<ResponseData<Song>>  saveHistory(@Body HistoryRequest historyRequest);

    @PUT(ConstantUrlAPI.URL_REMOVE_HISTORY)
    Call<Boolean>  removeHistory(@Body HistoryRequest historyRequest);

    @DELETE(ConstantUrlAPI.URL_REMOVE_ALL_HISTORY +"/" + "{userId}")
    Call<Boolean>  removeAllHistoryByUserID(@Path("userId") int userId);


    // comment

    @GET(ConstantUrlAPI.URL_GET_ALL_COMMENT_BY_SONG + "{songId}")
    Call<List<Comment>>  getAllCommentBySong(@Path("songId") long songId);

    @DELETE(ConstantUrlAPI.URL_REMOVE_COMMENT_BY_ID + "{commentId}")
    Call<Boolean>  removeCommentById(@Path("commentId") int commentId);

    @POST(ConstantUrlAPI.URL_ADD_COMMENT_BY_USER)
    Call<Comment>  addComment(@Body Comment comment);


    // playlist
    @GET(ConstantUrlAPI.URL_GET_ALL_PLAYLIST_BY_USER + "{userId}")
    Call<ResponseData<List<Playlist>>>  getAllPlaylistByUser(@Path("userId") Long userId);

    @GET(ConstantUrlAPI.URL_GET_ALL_PLAYLIST_BY_USER_NOT_EXIST_SONG + "{userId}")
    Call<ResponseData<List<Playlist>>>  getAllPlaylistByUserNotExistSong(@Path("userId") Long userId, @Query("songId") Long songId);

    @GET(ConstantUrlAPI.URL_GET_DETAIL_PLAYLIST + "{playlistId}")
    Call<ResponseData<List<DetailPlaylist>>>  getDetailPlaylist(@Path("playlistId") Long playlistId);

    @POST(ConstantUrlAPI.URL_ADD_PLAYLIST_USER)
    Call<ResponseData<Playlist>>  addPlaylistUser(@Body Playlist playlist);

    @POST(ConstantUrlAPI.URL_ADD_SONG_TO_PLAYLIST)
    Call<ResponseData<Playlist>>  addSongIntoPlaylist(@Body DetailPlaylistRequest detailPlaylistRequest);

    @DELETE(ConstantUrlAPI.URL_REMOVE_PLAYLIST + "{playlistId}")
    Call<Boolean>  removePlaylistUser(@Path("playlistId") Long playlistId);
    @DELETE(ConstantUrlAPI.URL_REMOVE_SONG_PLAYLIST + "{detailPlaylistId}")
    Call<Boolean>  removeSongFromPlaylist(@Path("detailPlaylistId") Long detailPlaylistId);

    // follow
    @POST(ConstantUrlAPI.URL_FOLLOW)
    Call<ResponseData<Playlist>>  follow(@Body Follow follow);

    @POST(ConstantUrlAPI.URL_UN_FOLLOW)
    Call<Boolean>  unFollow(@Body Follow follow);

    @GET(ConstantUrlAPI.URL_GET_ALL_FOLLOWER + "{userId}")
    Call<List<User>>  getAllFollower(@Path("userId") Long userId);
    @GET(ConstantUrlAPI.URL_GET_ALL_FOLLOWEE + "{userId}")
    Call<List<User>>  getAllFollowee(@Path("userId") Long userId);

    @POST(ConstantUrlAPI.URL_CHECK_FOLLOW_EXIST)
    Call<Boolean>  isExistFollow(@Body Follow follow);
}




