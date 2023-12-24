package Service;

import static com.example.musicplayer.activity.MyApplication.CHANNEL_ID_MUSIC;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;

import Constanst.Constant;
import Firebase.FirebaseService;
import LocalData.Entity.StatePlayMusic;
import Model.Song;

public class PlayMusicService extends Service {
    private static final String TAG = "PlayMusicService";
    private Song song;
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;
    private boolean isPause;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isCompeleteSong = false;
    private Song currentSong;
    private int seekToValue = -1;
    private Handler handler;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private CountDownTimer timer;
    private long timeCount = -1;


    private void startTimerCountDown(long millisInFuture) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeCount = millisUntilFinished;
                sendBroadcastToActivity(-1);
            }

            @Override
            public void onFinish() {
                handleChangeStatus(Constant.ACTION_STOP);
            }
        };

        timer.start();
    }

    @Override
    public void onCreate() {
        createNotification(currentSong);
        super.onCreate();
        handler = new Handler();

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (currentSong != null ) {
                        isCompeleteSong = true;
                        currentSong = null;
                        seekToValue = 0;
                        seekTo(0);
                        sendBroadcastToActivity(Constant.ACTION_STOP);
                    }
                }
            });
        }
    }


    private void createNotification(Song currentSong) {
        if (currentSong == null) return;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_MUSIC)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(contentIntent)
                .setContentTitle(currentSong.getTitle())
                .setContentText(currentSong.getUserUpload().getUsername())
                .setSubText(isPlaying ? "Đang chạy" : "Tạm dừng")
                .setLargeIcon(bitmap)
                .addAction(R.drawable.prev2_svgrepo_com, "Trước", getPendingIntentForAction(this, Constant.ACTION_PREVIOUS)) // #0
                .addAction(isPlaying ? R.drawable.pause_alt_svgrepo_com : R.drawable.play_svgrepo_com,
                        isPlaying ? "Tạm ừng" : "Bắt đầu",
                        isPlaying ? getPendingIntentForAction(this, Constant.ACTION_PAUSE) : getPendingIntentForAction(this, Constant.ACTION_RESUME))
                .addAction(R.drawable.next_svgrepo_com, "Tiếp theo", getPendingIntentForAction(this, Constant.ACTION_NEXT))     // #2
                // Apply the media style template.
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                );


        notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(1, notificationBuilder.build());
    }


    private void updateNotification(Song song) {
        if( song == null ) return;

        if (notificationBuilder == null) {
            createNotification(song);
        } else {
            notificationBuilder.clearActions();
            notificationBuilder.addAction(R.drawable.prev2_svgrepo_com, "Previous", getPendingIntentForAction(this, Constant.ACTION_PREVIOUS)) // #0
                    .addAction(isPlaying ? R.drawable.pause_alt_svgrepo_com : R.drawable.play_svgrepo_com,
                            isPlaying ? "Pause" : "Play",
                            isPlaying ? getPendingIntentForAction(this, Constant.ACTION_PAUSE) : getPendingIntentForAction(this, Constant.ACTION_RESUME))
                    .addAction(R.drawable.next_svgrepo_com, "Next", getPendingIntentForAction(this, Constant.ACTION_NEXT));
            notificationBuilder.setSubText(isPlaying ? "Đang chạy" : "Tạm dừng")
                    .setContentTitle(song.getTitle())
                    .setContentText(song.getUserUpload().getUsername());
        }

        // Gửi Notification
        notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(1, notificationBuilder.build());
    }


    private PendingIntent getPendingIntentForAction(Context context, int action) {
        Intent intent = new Intent(context, PlayMusicService.class);
        intent.putExtra(Constant.ACTION_TYPE, action);
        return PendingIntent.getService(context, action, intent, PendingIntent.FLAG_IMMUTABLE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            StatePlayMusic statePlayMusic = (StatePlayMusic) bundle.getSerializable(Constant.STATE_PLAY);

            song = (Song) bundle.getSerializable(Constant.CURRENT_SONG);
            long timer = bundle.getLong(Constant.TIMER_COUNT, -1);

            if( timer != -1  ) {
                startTimerCountDown(timer);
            }

            if( statePlayMusic != null ) {
                currentSong = song;
                updateNotification(currentSong);
            }
            if (song != null) {
                if( currentSong == null || currentSong.getId() != song.getId()) {
                    currentSong = song;
                    MusicActionManager.handleAction(this, Constant.ACTION_PLAY, mediaPlayer, song);
                    handleChangeStatus(Constant.ACTION_PLAY);
                }
                isPlaying = true;
                currentSong = song;
                updateNotification(currentSong);
            }
        }

        int action = intent.getIntExtra(Constant.ACTION_TYPE, 0);
        if (action != 0) {
            if( action == Constant.ACTION_SEEK_TO ) {
                seekToValue = intent.getIntExtra(Constant.SEEK_VALUE_TAG, -1);
            }
            handleChangeStatus(action);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Tag", "ON destroy service ");
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleChangeStatus(int actionCode) {
        switch (actionCode) {
            case Constant.ACTION_PLAY:
            case Constant.ACTION_RESUME: {
                isPlaying = true;
                isCompeleteSong = false;
                isPause = false;
                MusicActionManager.handleAction(this, actionCode, mediaPlayer, currentSong);
                updateNotification(currentSong);
                break;
            }
            case Constant.ACTION_PAUSE: {
                isPlaying = false;
                isPause = true;
                MusicActionManager.handleAction(this, Constant.ACTION_PAUSE, mediaPlayer, currentSong);
                updateNotification(currentSong);
                break;
            }
            case Constant.ACTION_STOP : {
                this.seekToValue = -1;
                seekTo(0);
                isPlaying = false;
                isPause = false;
                isCompeleteSong = false;
                if( mediaPlayer.isPlaying() )
                    mediaPlayer.stop();
                break;
            }
            case Constant.ACTION_REPEAT: {
                isRepeat = !isRepeat;
                break;
            }
            case Constant.ACTION_SHUFFLE: {
                isShuffle = !isShuffle;
                break;
            }
            case Constant.ACTION_SEEK_TO: {
                break;
            }
            case Constant.ACTION_CANCEL_TIMER: {
                timer.cancel();
                break;
            }
//            default: {
//                Toast.makeText(this, "Action not found!", Toast.LENGTH_SHORT).show();
//            }
        }
        sendBroadcastToActivity(actionCode);
    }

    private void sendBroadcastToActivity(int action) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_MUSIC);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.ACTION_TYPE, action);
        if (currentSong != null) {
            bundle.putSerializable(Constant.CURRENT_SONG, currentSong);
        }
        if( seekToValue == -1 ) {
            bundle.putInt(Constant.SEEK_TO, mediaPlayer.getCurrentPosition() / 1000);
        }else {
            bundle.putInt(Constant.SEEK_TO, seekToValue);
            seekTo(seekToValue);
            seekToValue = -1;
        }
        bundle.putBoolean(Constant.IS_PLAYING, isPlaying);
        bundle.putBoolean(Constant.IS_SHUFFLE, isShuffle);
        bundle.putBoolean(Constant.IS_REPEAT, isRepeat);
        bundle.putBoolean(Constant.IS_COMPLETE_SONG, isCompeleteSong);
        bundle.putBoolean(Constant.IS_PAUSE, isPause);
        bundle.putLong(Constant.TIMER_COUNT, timeCount);

        intent.putExtras(bundle);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }


    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_MUSIC);
        return intentFilter;
    }


    private void seekTo(int seekToValue) {
        if (mediaPlayer != null && mediaPlayer.isPlaying() ) {
            mediaPlayer.seekTo(seekToValue * 1000L, MediaPlayer.SEEK_CLOSEST);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.start();
                    isPlaying = true;
                }
            });
        }
    }

    public static void pauseMusic(Context context) {
        Intent pauseIntent = new Intent(context, PlayMusicService.class);
        pauseIntent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_PAUSE);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }

    public static void resumeMusic(Context context) {
        Intent pauseIntent = new Intent(context, PlayMusicService.class);
        pauseIntent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_RESUME);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }

    public static void isRepeat(Context context) {
        Intent pauseIntent = new Intent(context, PlayMusicService.class);
        pauseIntent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_REPEAT);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }
    public static void isShuffle(Context context) {
        Intent pauseIntent = new Intent(context, PlayMusicService.class);
        pauseIntent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_SHUFFLE);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }

    public static void seekTo(Context context, int value) {
        Intent pauseIntent = new Intent(context, PlayMusicService.class);
        pauseIntent.putExtra(Constant.SEEK_VALUE_TAG, value);
        pauseIntent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_SEEK_TO);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }

    public static void playMusic(Context context, Song song) {

        FirebaseService firebaseService = new FirebaseService(context);
        firebaseService.changeCurrentSong(song, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {
                if( isSuccess ) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.CURRENT_SONG,song);
                    Intent intent = new Intent(context, PlayMusicService.class);
                    intent.putExtras(bundle);
                    // Start the service
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                }
            }
        });

    }

    public static void stopMusic(Context context) {
        Intent pauseIntent = new Intent(context, PlayMusicService.class);
        pauseIntent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_STOP);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }

    public static void updateStatePlayMusic(Context context, StatePlayMusic statePlayMusic, Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CURRENT_SONG,song);
        bundle.putSerializable(Constant.STATE_PLAY,statePlayMusic);
        Intent intent = new Intent(context, PlayMusicService.class);
        intent.putExtras(bundle);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void startTimer(Context context, long time) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constant.TIMER_COUNT,time);
        Intent intent = new Intent(context, PlayMusicService.class);
        intent.putExtras(bundle);
        intent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_START_TIMER);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void cancelTimer(Context context) {
        Intent intent = new Intent(context, PlayMusicService.class);
        intent.putExtra(Constant.ACTION_TYPE, Constant.ACTION_CANCEL_TIMER);
        // Start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}
