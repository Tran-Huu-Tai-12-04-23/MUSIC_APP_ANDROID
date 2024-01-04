package com.example.musicplayer.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import BottomSheet.BottomSheetPlayMusic;
import BottomSheet.BottomSheetPromiseTime;
import Constanst.Constant;
import Firebase.FirebaseService;
import Interface.BottomSheetTimeCounterInteractionListener;
import LocalData.DTO.StatePlayMusicDao;
import LocalData.Database.AppDatabase;
import LocalData.Entity.StatePlayMusic;
import Model.CurrentPlaylist;
import Model.Song;
import Model.User;
import Service.ApiService;
import Service.PlayMusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;
import utils.Util;

public class HomeActivity extends AppCompatActivity {
    private  BottomSheetTimeCounterInteractionListener bottomSheetTimeCounterInteractionListener;
    private LoadingDialog loadingDialog;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigate;
    private LinearLayout floatingPayer;
    private LinearProgressIndicator progressDuration;
    private TextView tvNameSong;
    private MaterialButton btnFloatingNextSong, btnFloatingPlaySong;
    private ShapeableImageView thumbnailsCurrentSong;
    ///
    private boolean isShowDialogPlayMusic = false;
    private NavController navController;
    private Handler handler;
    private boolean isPlaying;
    private boolean isPause;
    private boolean isRepeat;
    private boolean isShuffle;
    private boolean isCompleteSong = false;
    private Song currentSong;
    private int seekToValue;
    private long timerCount = -1;
    private int currentFragment;
    private FirebaseService firebaseService;
    private StatePlayMusic statePlayMusic;
    private List<Song> playingListSong;
    private NavHostFragment navHostFragment;
    private LinearLayout floatingMusicContainer;
    AppDatabase dbLocal ;

    private User user;

    public long getTimerCount() {
        return timerCount;
    }

    public StatePlayMusic getStatePlayMusic() {
        return statePlayMusic;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public User getUser() {
        return user;
    }

    private Queue<Integer> fragmentQueue;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int actionType = bundle.getInt(Constant.ACTION_TYPE, 0);
                Song song = (Song) bundle.getSerializable(Constant.CURRENT_SONG);
                if( song != null) {
                    currentSong = song;
                    initViewFloatingMusic(song);
                };
                isPlaying = bundle.getBoolean(Constant.IS_PLAYING, false);
                seekToValue = bundle.getInt(Constant.SEEK_TO, 0);
                isPause = bundle.getBoolean(Constant.IS_PAUSE, false);
                isShuffle = bundle.getBoolean(Constant.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(Constant.IS_REPEAT, false);
                isCompleteSong = bundle.getBoolean(Constant.IS_COMPLETE_SONG, false);
                timerCount = bundle.getLong(Constant.TIMER_COUNT, -1);
//                 Xử lý dữ liệu nhận được từ broadcast
//                Log.d("MyBroadcastReceiver", "Received Broadcast - ActionType: " + actionType
//                        + ", Song: " + currentSong + ", IsPlaying: " + isPlaying
//                        + ", SeekTo: " + seekToValue + ", IsPause: " + isPause
//                        + ", IsShuffle: " + isShuffle + ", IsRepeat: " + isRepeat + ", timerCount: " + timerCount);

                if( isCompleteSong ) {
                    handleCompleteSong();
                    return;
                }

                updateStatePlayMusicGlobal(isPlaying, isPause, isShuffle, isRepeat, seekToValue);

                updateProgress(seekToValue);
                handleActionReceive(actionType);


            }else {
                Log.d("Receiver", "Bundle is null");
            }
        }
    };

    private void updateStatePlayMusicGlobal(boolean isPlaying, boolean isPause, boolean isShuffle, boolean isRepeat, int seekToValue) {
        if( statePlayMusic != null ) {
            statePlayMusic.setShuffle(isShuffle);
            statePlayMusic.setPause(isPause);
            statePlayMusic.setPlaying(isPlaying);
            statePlayMusic.setRepeat(isRepeat);
            statePlayMusic.setSeekToValue(seekToValue);
        }
    }

    private void handleActionReceive(int action) {
        switch (action) {
            case Constant.ACTION_PLAY: {
                initViewFloatingMusic(currentSong);
                handler.postDelayed(sendRequestUpdateSeekBar, 1000);
                floatingMusicContainer.setVisibility(View.VISIBLE);
                break;
            }
            case Constant.ACTION_RESUME: {
                handler.postDelayed(sendRequestUpdateSeekBar, 1000);
                break;
            }
            case Constant.ACTION_PAUSE:
            case Constant.ACTION_STOP: {
                break;
            }
            case Constant.ACTION_NEXT:{
                handleNextSong();
                break;
            }
            case Constant.ACTION_PREVIOUS: {
                handlePrevSong();
                break;
            }
            case Constant.ACTION_CANCEL_TIMER: {
                timerCount = -1;
                Log.i("TIME COUT", String.valueOf(timerCount));
//                updateUiPromiseTime();
                break;
            }

            case Constant.ACTION_OPEN_APP: {
                Intent appIntent = new Intent(this, HomeActivity.class);
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(appIntent);
                break;
            }
            case Constant.UPDATE_STATE_PLAY_MUSIC : {
                if( isPlaying ) {
//                    handler.postDelayed(sendRequestUpdateSeekBar, 1000);
                }
                break;
            }
        }

        updateBtnPlay();
    }
    private void handlePrevSong() {
        int indexSong = -1;
        if( isShuffle ) {
            indexSong = Util.randomIndexSong(currentSong, playingListSong);
        }else {
            indexSong = Util.getPrevSong(currentSong, playingListSong);
        }
        if( indexSong != -1 ) {
            currentSong = playingListSong.get(indexSong);
        }
        PlayMusicService.playMusic(getApplicationContext(), currentSong);
        firebaseService.changeCurrentSong(currentSong, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });
    }
    private  void handleCompleteSong() {
        if( isCompleteSong &&  isRepeat  && currentSong != null ) {
            PlayMusicService.playMusic(getApplicationContext(), currentSong);
            isCompleteSong = false;
        }
        else {
            PlayMusicService.stopMusic(getApplicationContext());
            updateBtnPlay();
            progressDuration.setProgress(0);
        }
    }
    private Runnable sendRequestUpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                seekToForward();
                handler.postDelayed(this, 1000);
            }
        }

    };

    private void seekToForward() {
        Intent intent = new Intent(getApplicationContext(), PlayMusicService.class);
        intent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_SEEK_TO);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(intent);
        } else {
            getApplicationContext().startService(intent);
        }
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentQueue = new LinkedList<>();
        SharedPreferences preferences = getSharedPreferences("LoginData", MODE_PRIVATE);

        String strUserId = preferences.getString("userId", "");
        if( !strUserId.equals("")) {
            initUser(Long.parseLong(strUserId));
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        dbLocal = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, Constant.APP_DB)
                .fallbackToDestructiveMigration()
                .build();

        firebaseService = new FirebaseService(getApplicationContext());
        handler = new Handler();

        drawerLayout = findViewById(R.id.drawer_page);
//        toolbar = findViewById(R.id.toolbar);
        loadingDialog = new LoadingDialog(this);
        bottomNavigate = findViewById(R.id.bottom_navigation);
        floatingPayer = findViewById(R.id.floating_player);
        progressDuration = findViewById(R.id.progress_durationSong);
        tvNameSong = findViewById(R.id.tv_name_song);
        btnFloatingNextSong = findViewById(R.id.btn_floating_music_next);
        btnFloatingPlaySong = findViewById(R.id.btn_floating_play_music);
        thumbnailsCurrentSong = findViewById(R.id.thumbnails_song);
        floatingMusicContainer = findViewById(R.id.floating_player_container);

        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        bottomNavigate.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int idItem = item.getItemId();
                if( idItem == currentFragment) return false;
                switchFragment(idItem);
                YoYo.with(Techniques.SlideInLeft)
                        .duration(200)
                        .playOn(findViewById(R.id.fragmentContainer));
                return true;
            }
        });
//

        View rootView = getWindow().getDecorView().getRootView();
//
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    bottomNavigate.setVisibility(View.GONE);
                } else {
                    bottomNavigate.setVisibility(View.VISIBLE);
                }
            }
        });
//       open fragment player when click floating player
        floatingPayer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                openPlayMusicDialog();
            }
        });

        btnFloatingPlaySong.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( isPlaying ) {
                PlayMusicService.pauseMusic(getApplicationContext());
            }else {
                PlayMusicService.resumeMusic(getApplicationContext());
            }
        });
        btnFloatingNextSong.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( playingListSong != null)
                handleNextSong();
        });

        initFloatingPlayMusicUI();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, PlayMusicService.getIntentFilter());

    }

    private void handleNextSong() {
        int indexSong = -1;
        if( isShuffle ) {
            indexSong = Util.randomIndexSong(currentSong, playingListSong);
        }else {
            indexSong = Util.getNextSong(currentSong, playingListSong);
        }

        if( indexSong != -1 ) {
            currentSong = playingListSong.get(indexSong);
        }
        isPlaying = true;
        PlayMusicService.playMusic(getApplicationContext(), currentSong);
        firebaseService.changeCurrentSong(currentSong, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void updateStatePlayMusicLocal() {
        if( user==null ) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                StatePlayMusicDao statePlayMusicDao = dbLocal.statePlayMusicDao();
                StatePlayMusic statePlayMusic = new StatePlayMusic();
                statePlayMusic.setPlaying(isPlaying);
                statePlayMusic.setPause(isPause);
                statePlayMusic.setSeekToValue(seekToValue);
                statePlayMusic.setRepeat(isRepeat);
                statePlayMusic.setShuffle(isShuffle);
                statePlayMusic.setId(Math.toIntExact(user.getId()));
                statePlayMusicDao.update(statePlayMusic);
                return null;
            }
        }.execute();

    }
    @SuppressLint("StaticFieldLeak")
    private void initStatePlayMusic() {
        if( user==null ) return;
        Log.i("TAG", String.valueOf(Math.toIntExact(user.getId())));
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                StatePlayMusicDao statePlayMusicDao = dbLocal.statePlayMusicDao();
                statePlayMusic = statePlayMusicDao.getStatePlayMusicById(Math.toIntExact(user.getId()));
                if( statePlayMusic == null ) {
                    StatePlayMusic statePlayMusic = new StatePlayMusic();
                    statePlayMusic.setPlaying(isPlaying);
                    statePlayMusic.setPause(isPause);
                    statePlayMusic.setSeekToValue(seekToValue);
                    statePlayMusic.setRepeat(isRepeat);
                    statePlayMusic.setShuffle(isShuffle);
                    statePlayMusic.setId(Math.toIntExact(user.getId()));
                    statePlayMusicDao.insert(statePlayMusic);
                }else {
                    Log.i("State play music" , statePlayMusic.toString());
                    updateStateForService(statePlayMusic);
                }
                return null;
            }
        }.execute();

    }

    private void updateStateForService(StatePlayMusic statePlayMusic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideInUp).duration(1000).playOn(floatingMusicContainer);
                floatingMusicContainer.setVisibility(View.VISIBLE);
                seekToValue = statePlayMusic.getSeekToValue();
                updateProgress(statePlayMusic.getSeekToValue());
            }
        });

        PlayMusicService.updateStatePlayMusic(getApplicationContext(), statePlayMusic, currentSong);
    }

    //handle ui

    private void initViewFloatingMusic(Song currentSong) {
        if( currentSong == null) return;
        tvNameSong.setText(currentSong.getTitle());
        progressDuration.setMax((int) currentSong.getDuration());
        if(seekToValue <  progressDuration.getMax() ) {
            progressDuration.setProgress(seekToValue);
        }
        Glide.with(getApplicationContext())
                .load(currentSong.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnailsCurrentSong);
    }

    private void updateProgress(int seekTo) {
        if( progressDuration.getMax() > seekTo )
            progressDuration.setProgress(seekTo);
        else if( progressDuration.getMax() == seekTo ) {
            progressDuration.setProgress(0);
        }
    }
    private void updateBtnPlay() {
        if( isPlaying ) {
            btnFloatingPlaySong.setIconResource(R.drawable.pause_alt_svgrepo_com);
        }else {
            btnFloatingPlaySong.setIconResource(R.drawable.play_svgrepo_com);
        }
    }
    //end handle ui

    private void initFloatingPlayMusicUI() {
        firebaseService.getCurrentPlaylist(new FirebaseService.OnGetPlaylistCompleteListener() {
            @Override
            public void onGetPlaylistComplete(CurrentPlaylist currentPlaylist) {
                if( currentPlaylist != null){
                    currentSong = currentPlaylist.getCurrentSong();
                    initViewFloatingMusic(currentSong);
                    playingListSong = currentPlaylist.getSongs();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if( drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (navController.popBackStack()) {
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_library) {
                bottomNavigate.setSelectedItemId(R.id.nav_library);
            }else if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_search) {
                bottomNavigate.setSelectedItemId(R.id.nav_search);
            }else if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_home) {
                bottomNavigate.setSelectedItemId(R.id.nav_home);
            }else if( navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_profile ) {
                bottomNavigate.setSelectedItemId(R.id.nav_profile);
            }
        }

        super.onBackPressed();
    }

    public void switchFragment(int idFrag) {
        if( navController == null ) return;
        YoYo.with(Techniques.SlideInRight)
                .duration(200)
                .playOn(findViewById(R.id.fragmentContainer));
        navController.navigate(idFrag);
        currentFragment = idFrag;
    }

    private void openPlayMusicDialog() {
        BottomSheetPlayMusic bottomSheetPlayMusic = new BottomSheetPlayMusic();
        bottomSheetPlayMusic.setSong(currentSong);
        bottomSheetPlayMusic.show(getSupportFragmentManager(), "Bottom sheet play music!");
    }

    public void logout() {
        // Update SharedPreferences to mark the user as not logged in
        SharedPreferences preferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();
//        // Navigate to the login screen or perform any other necessary logout actions
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish();
    }


    public void openPlaylistFragment() {
        switchFragment(R.id.nav_playlist);
    }

    public void openSongLikedFragment() {
        switchFragment(R.id.nav_like_song);
    }

    public void openFollowing() {
        switchFragment(R.id.nav_following_screen);
    }
    public void openSearchScreen() {
        switchFragment(R.id.nav_search);
        bottomNavigate.setSelectedItemId(R.id.nav_search);
    }
    public void openPlaylistDetail( Bundle bundle) {
        if( navController == null )return;
        YoYo.with(Techniques.SlideInRight)
                .duration(200)
                .playOn(findViewById(R.id.fragmentContainer));
        navController.navigate(R.id.nav_playlist_detail, bundle);
        currentFragment = R.id.nav_playlist_detail;

    }

    public void openDetailArtist( Bundle bundle) {
        if( navController == null )return;
        YoYo.with(Techniques.SlideInRight)
                .duration(200)
                .playOn(findViewById(R.id.fragmentContainer));
        navController.navigate(R.id.nav_detail_artist, bundle);
        currentFragment = R.id.nav_detail_artist;



    }

    private void initUser(Long userId) {
        ApiService.ApiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if( response.isSuccessful() ) {
                    user = response.body();
                    initStatePlayMusic();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i("HOme activity" , t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.destroyDialog();
        int notificationId = 1;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, PlayMusicService.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        this.updateStatePlayMusicLocal();
    }

}