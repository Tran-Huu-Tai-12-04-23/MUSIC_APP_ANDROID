package BottomSheet;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Constanst.Constant;
import Firebase.FirebaseService;
import Interface.BottomSheetPlayMusicInteractionListener;
import LocalData.Entity.StatePlayMusic;
import Model.CurrentPlaylist;
import Model.Liked;
import Model.Song;
import Model.User;
import Service.ApiService;
import Service.PlayMusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Util;

public class BottomSheetPlayMusic extends BottomSheetDialogFragment implements BottomSheetPlayMusicInteractionListener {
    private BottomSheetBehavior<View> bBehavior;
    private Song currentSong;
    private User user;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isPause = false;
    private boolean isCompleteSong = false;
    private boolean isLiked ;
    private int seekToValue = 0;
    private int valueSeekToPress = -1;

    ShapeableImageView btnPrev, btnPlay, btnNext, btnShuffle, btnRepeat ;
    MaterialButton btnClosePlayMusic, btnOpenPlayingList, btnOpenMusicComment, btnOpenMenu, btnLike;
    ImageView thumbnails;
    TextView tvNameSong, tvNameArtist, tvDurationStart, tvDurationEnd;
    Slider sliderDurationMusic;
    TextView tvNameSongDetail, tvUploadDate, tvNameArtistDetail, tvNameSongDetailTitle, tvNameArtistDetailTitle;
    ShapeableImageView thumbnailsDetail;
    ObjectAnimator rotateAnimator;
    private long currentAnimatorTime = 0;
    private FirebaseService firebaseService;
    private CurrentPlaylist currentPlayingList;


    public void setCurrentPlayingList(CurrentPlaylist currentPlayingList) {
        this.currentPlayingList = currentPlayingList;
    }
    public void setSong(Song song) {
        this.currentSong = song;
    }
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int actionType = bundle.getInt(Constant.ACTION_TYPE, 0);
                Song song = (Song) bundle.getSerializable(Constant.CURRENT_SONG);
                if( song != null) {
                    currentSong = song;
                };
                isPlaying = bundle.getBoolean(Constant.IS_PLAYING, false);
                seekToValue = bundle.getInt(Constant.SEEK_TO, 0);
                isPause = bundle.getBoolean(Constant.IS_PAUSE, false);
                isShuffle = bundle.getBoolean(Constant.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Constant.IS_REPEAT, false);
                isCompleteSong = bundle.getBoolean(Constant.IS_COMPLETE_SONG, false);

//                 Xử lý dữ liệu nhận được từ broadcast
                Log.d("MyBroadcastReceiver bottomSheet", "Received Broadcast - ActionType: " + actionType
                        + ", Song: " + currentSong + ", IsCompelete: " + isCompleteSong + ", IsPlaying: " + isPlaying
                        + ", SeekTo: " + seekToValue + ", IsPause: " + isPause
                        + ", IsShuffle: " + isShuffle + ", IsRepeat: " + isRepeat);
                if( isCompleteSong ) {
                    handleCompleteSong();
                }
                updateDurationSlide(seekToValue);
                handleAction(actionType);
            }else {
                Log.d("Receiver", "Bundle is null");
            }
        }
    };

    //handle action form broadcast
    private void handleAction(int action) {
        switch (action) {
            case Constant.ACTION_PLAY:
            case Constant.ACTION_RESUME:
            case Constant.ACTION_PAUSE: {
                updateBtnPlay();
                break;
            }
            case Constant.ACTION_REPEAT: {
                updateBtnRepeat();
                break;
            }
            case Constant.ACTION_SHUFFLE: {
                updateBtnShuffle();
                break;
            }
            default: {
                updateBtnPlay();
                updateBtnShuffle();
                updateBtnRepeat();
            }
        }
    }
    private void handleCompleteSong() {
//        if( isCompleteSong && !isRepeat && !isShuffle && currentSong != null ) {
//            PlayMusicService.stopMusic(requireContext(), currentSong);
//            isPlaying = false;
//            updateBtnPlay();
//        }
//        rotateAnimator.cancel();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if( requireActivity() instanceof  HomeActivity ) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();
        }

        super.onAttach(context);
    }

    @SuppressLint({"SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mediaPlayer = new MediaPlayer();
        firebaseService = new FirebaseService(requireContext());
        initData();
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_playmusic, null);
        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());
        bBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        render detail song
        tvNameArtistDetail = view.findViewById(R.id.tv_name_artist_detail);
        tvUploadDate = view.findViewById(R.id.tv_upload_date);
        tvNameSongDetail = view.findViewById(R.id.tv_name_song_detail);
        thumbnailsDetail = view.findViewById(R.id.thumbnails);
        ///end detail song
        sliderDurationMusic = view.findViewById(R.id.slider_duration_music);
        btnNext = view.findViewById(R.id.btn_next_music);
        btnPlay = view.findViewById(R.id.btn_start_music);
        btnPrev = view.findViewById(R.id.btn_prev_music);
        btnShuffle = view.findViewById(R.id.btn_shuffle_music);
        btnRepeat = view.findViewById(R.id.btn_repeat_music);
        btnClosePlayMusic = view.findViewById(R.id.btn_close_play_music);
        btnOpenPlayingList = view.findViewById(R.id.btn_open_playing_list);
        btnOpenMusicComment = view.findViewById(R.id.btn_open_comment_music);
        btnOpenMenu = view.findViewById(R.id.btn_open_menu);
        thumbnails  = view.findViewById(R.id.song_thumbnails);
        tvNameArtist = view.findViewById(R.id.tv_name_artist);
        tvNameSong = view.findViewById(R.id.tv_name_song);
        tvDurationEnd = view.findViewById(R.id.tv_duration_end);
        tvDurationStart = view.findViewById(R.id.tv_duration_start);
        tvNameSongDetailTitle = view.findViewById(R.id.tv_name_song_detail_title);
        tvNameArtistDetailTitle = view.findViewById(R.id.tv_name_artist_detail_title);
        btnLike = view.findViewById(R.id.btn_add_favourite_music);

        btnOpenMusicComment.setOnClickListener(v -> {
            BottomSheetComment bottomSheetComment = new BottomSheetComment();
            bottomSheetComment.setCurrentSong(currentSong);
            bottomSheetComment.show(getChildFragmentManager(), "Bottom sheet comment!");
        });

        btnClosePlayMusic.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btnPlay.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( isPause ) {
                resumeMusic();
            } else if(isPlaying) {
                pauseMusic();
            } else {
                playSong();
            }
        });
        btnNext.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            handleNextSong();
        });
        btnPrev.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            handlePrevSong();
        });

        btnShuffle.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            PlayMusicService.isShuffle(requireContext());
        });

        btnRepeat.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            PlayMusicService.isRepeat(requireContext());
        });

        btnOpenPlayingList.setOnClickListener(v -> {
            BottomSheetPlayingList bottomSheetPlayingList= new BottomSheetPlayingList();
            bottomSheetPlayingList.setCurrentSong(currentSong);
            bottomSheetPlayingList.show(getChildFragmentManager(), "Playing list open");
        });

        sliderDurationMusic.setLabelFormatter(value -> {
            return String.format("%s",  Util.convertDurationToString((long) value));
        });

        sliderDurationMusic.addOnChangeListener((slider1, value, fromUser) -> {
            if (fromUser) {
                PlayMusicService.seekTo(requireContext(), (int) value);
            }
        });

        btnOpenMenu.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( currentSong == null ) return;
            BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
            bottomSheetActionSong.setSong(currentSong);
            bottomSheetActionSong.show(getChildFragmentManager(), "Open action song");
        });

        btnLike.setOnClickListener(v -> {
            if( isLiked ) {
                unLike();
            }else {
                like();
            }
        });

        initCheckLiked();

        if( currentSong != null ) {
            handleUpdateUi();
        }

        HomeActivity homeActivity = (HomeActivity) requireActivity();
        StatePlayMusic statePlayMusic = homeActivity.getStatePlayMusic();
        if( statePlayMusic != null ) initState(statePlayMusic);

        return dialog;
    }
    private void like() {
        if(user == null || currentSong == null ) return;
        Liked liked = new Liked();
        liked.setSong(currentSong);
        liked.setUser(user);
        ApiService.ApiService.like(liked).enqueue(new Callback<Liked>() {
            @Override
            public void onResponse(Call<Liked> call, Response<Liked> response) {
                if( response.isSuccessful() ) {
                    isLiked = true;
                    handleUpdateUiLikeBtn();
                }
            }
            @Override
            public void onFailure(Call<Liked> call, Throwable t) {

            }
        });
    }
    private void unLike() {
        if( user == null ) return;

        Liked like = new Liked();
        like.setUser(user);
        like.setSong(currentSong);
        ApiService.ApiService.unLike(like).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( response.isSuccessful()) {
                    if( response.body() == null) return;
                    isLiked = false;
                    handleUpdateUiLikeBtn();
                    Snackbar.make(getDialog().getWindow().getDecorView() , "Bạn đã bỏ thích bài hát " + currentSong.getTitle(), Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undoUnlike();
                        }
                    }).show();

                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    private void undoUnlike() {
        like();
    }

    private void initCheckLiked() {
        if( user == null ) return;

        Liked like = new Liked();
        like.setUser(user);
        like.setSong(currentSong);
        ApiService.ApiService.isCheckUserLikedSong(like).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( response.isSuccessful()) {
                    if( response.body() == null) return;
                    isLiked = response.body();
                    handleUpdateUiLikeBtn();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    private void handleUpdateUiLikeBtn() {
        if( isLiked ) {
            btnLike.setIconTint(Util.getIconActiveColor(requireContext()));
        }else {
            btnLike.setIconTint(Util.getIconDeactiveColor(requireContext()));
        }
    }

    private void handleNextSong() {
        int indexSong = -1;
        if( isShuffle ) {
            indexSong = Util.randomIndexSong(currentSong, currentPlayingList.getSongs());
        }else {
            indexSong = Util.getNextSong(currentSong, currentPlayingList.getSongs());
        }

        if( indexSong != -1 ) {
            currentSong = currentPlayingList.getSongs().get(indexSong);
        }
        handleUpdateUi();
        PlayMusicService.playMusic(requireContext(), currentSong);
        firebaseService.changeCurrentSong(currentSong, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });
    }

    private void handlePrevSong() {
        int indexSong = -1;
        if( isShuffle ) {
            indexSong = Util.randomIndexSong(currentSong, currentPlayingList.getSongs());
        }else {
            indexSong = Util.getPrevSong(currentSong, currentPlayingList.getSongs());
        }

        if( indexSong != -1 ) {
            currentSong = currentPlayingList.getSongs().get(indexSong);
        }
        handleUpdateUi();
        PlayMusicService.playMusic(requireContext(), currentSong);
        firebaseService.changeCurrentSong(currentSong, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });
    }
    //handle update UI
    private void updateDurationSlide(int value) {
        if( value <= sliderDurationMusic.getValueTo() ) {
            sliderDurationMusic.setValue(value);
        }
        tvDurationStart.setText(Util.convertDurationToString(value));
    }

    private void updateBtnPlay() {
        if( isPlaying ) {
            btnPlay.setImageResource(R.drawable.pause_alt_svgrepo_com);
        }else {
            btnPlay.setImageResource(R.drawable.play_svgrepo_com);
        }
    }
    private void updateBtnRepeat() {
        if (isRepeat) {
            int color = ContextCompat.getColor(requireContext(), R.color.icon_primary);
            btnRepeat.setColorFilter(color);
        } else {
            int color = ContextCompat.getColor(requireContext(), R.color.icon_second);
            btnRepeat.setColorFilter(color); // Hoặc btnRepeat.setImageResource(R.drawable.your_icon);
        }
    }
    private void updateBtnShuffle() {
        if (isShuffle) {
            int color = ContextCompat.getColor(requireContext(), R.color.icon_primary);
            btnShuffle.setColorFilter(color);
        } else {
            int color = ContextCompat.getColor(requireContext(), R.color.icon_second);
            btnShuffle.setColorFilter(color); // Hoặc btnRepeat.setImageResource(R.drawable.your_icon);
        }
    }
    //end handle update UI
    @SuppressLint("SetTextI18n")
    private void handleUpdateUi() {
//      if (!isAdded() || getContext() == null) return;
        if ( getContext() == null || currentSong == null ) return;
        tvDurationEnd.setText(Util.convertDurationToString((long)currentSong.getDuration()));
        tvDurationStart.setText(Util.convertDurationToString(seekToValue));

        Glide.with(requireContext())
                .load(currentSong.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnailsDetail);
        tvNameArtistDetail.setText(currentSong.getUserUpload().getUsername());
        Date uplodaDate = currentSong.getUploadDate();
        tvUploadDate.setText(uplodaDate.getDay() + "/" + uplodaDate.getMonth() + "/" + (uplodaDate.getYear()+1900) );
        tvNameSongDetail.setText(currentSong.getTitle());
        tvNameArtistDetailTitle.setText(currentSong.getUserUpload().getUsername());
        tvNameSongDetailTitle.setText(currentSong.getTitle());

        tvNameArtist.setText(currentSong.getUserUpload().getUsername());
        tvNameSong.setText(currentSong.getTitle());

        sliderDurationMusic.setValueTo((float) currentSong.getDuration());
        if( seekToValue < sliderDurationMusic.getValueTo()) {
            sliderDurationMusic.setValue(seekToValue);
        }

        Glide.with(requireContext())
                .load(currentSong.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnails);

        updateBtnPlay();
        updateBtnShuffle();
        updateBtnRepeat();
    }

    private void playSong() {
        PlayMusicService.playMusic(requireContext(), currentSong);
    }
    private void pauseMusic() {
        PlayMusicService.pauseMusic(requireContext());
    }

    private void resumeMusic() {
        PlayMusicService.resumeMusic(requireContext());
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcastReceiver, PlayMusicService.getIntentFilter());
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onDestroy() {
        // Release the media player when the fragment is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void initData() {
        firebaseService.getCurrentPlaylist(new FirebaseService.OnGetPlaylistCompleteListener() {
            @Override
            public void onGetPlaylistComplete(CurrentPlaylist currentPlaylist) {
                currentPlayingList = currentPlaylist;
                if (currentPlaylist == null) {
                    initPlayingSong();
                } else {
                    if( currentPlaylist.getSongs() != null && currentSong != null){
                        boolean isSongExist = songExistList(currentSong, currentPlaylist.getSongs());
                        if( !isSongExist)
                            addSongIntoList(currentSong, currentPlaylist);
                    }
                    if( currentSong == null && currentPlaylist.getCurrentSong() != null) {
                        currentSong = currentPlaylist.getCurrentSong();
                    }else {
                        changeCurrentSongPlayingList(currentSong);
                        handleUpdateUi();
                    }

                    if( currentPlaylist.getCurrentSong().getId() != currentSong.getId()) {
                        PlayMusicService.playMusic(requireContext(), currentSong);
                    }

                }
            }
        });

    }

    private void addSongIntoList(Song song, CurrentPlaylist currentPlaylist) {
        firebaseService.addSongToPlaylist( song, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {
            }
        });


    }

    private void changeCurrentSongPlayingList(Song song) {
        firebaseService.changeCurrentSong(song, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });

    }
    private boolean songExistList(Song song, List<Song> songs) {
        for( Song songEntity : songs ) {
            if( songEntity.getTitle().equals(song.getTitle())
                    && songEntity.getSongLink().equals(song.getSongLink())
                    && songEntity.getThumbnails().equals(song.getThumbnails())) {
                return true;
            }
        }
        return false;
    }

    private void initPlayingSong() {
        CurrentPlaylist currentPlaylist = new CurrentPlaylist();

        currentPlaylist.setCurrentSong(currentSong);
        List<Song> songs = new ArrayList<>();
        songs.add(currentSong);
        currentPlaylist.setSongs(songs);

        firebaseService.saveCurrentSong(currentPlaylist, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });
    }

    @Override
    public void onCurrentSongChange(Song song) {
        this.currentSong = song;
        handleUpdateUi();
    }

    @Override
    public void onCurrentListChange(CurrentPlaylist currentPlaylist) {
        this.currentPlayingList = currentPlaylist;
    }
    public void initState(StatePlayMusic statePlayMusic) {
        isPlaying = statePlayMusic.isPlaying();
        isPause = statePlayMusic.isPause();
        isRepeat = statePlayMusic.isRepeat();
        isShuffle = statePlayMusic.isShuffle();
        seekToValue = statePlayMusic.getSeekToValue();
        handleUpdateUi();
    }
}
