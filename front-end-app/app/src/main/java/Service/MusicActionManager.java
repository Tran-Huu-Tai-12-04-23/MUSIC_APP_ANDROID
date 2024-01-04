package Service;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

import Constanst.Constant;
import Model.Song;

public class MusicActionManager {

    private static AsyncTask<String, Void, Boolean> currentPrepareMusicTask;

    public static void handleAction(Context context, int actionCode, MediaPlayer mediaPlayer, Song song) {

        switch (actionCode) {
            case Constant.ACTION_PLAY: {
                play(context, mediaPlayer, song);
                break;
            }
            case Constant.ACTION_PAUSE: {
                pause(mediaPlayer, context);
                break;
            }
            case Constant.ACTION_RESUME: {
                resume(mediaPlayer, context);
                break;
            }
            case Constant.ACTION_STOP: {
                stop(mediaPlayer, context);
                break;
            }
//            default: {
//                Toast.makeText(context, "Action not found!", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private static void play(Context context, MediaPlayer mediaPlayer, Song song) {

        if (currentPrepareMusicTask != null ) {
            currentPrepareMusicTask.cancel(true);
        }
        // Your play logic here
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            if (!song.getSongLink().isEmpty()) {
                currentPrepareMusicTask = new PrepareMusicTask(context, mediaPlayer);
                currentPrepareMusicTask.execute(song.getSongLink() + Constant.authStringApiSongLink);
//                Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "Đường dẫn không tìm thấy!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void pause(MediaPlayer mediaPlayer, Context context) {
        // Your pause logic here
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
//            Toast.makeText(context, "Pause music!", Toast.LENGTH_SHORT).show();
        }
    }

    private static void resume(MediaPlayer mediaPlayer, Context context) {
        // Your resume logic here
        if (mediaPlayer != null) {
            int length = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
//            Toast.makeText(context, "Resume music!", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "Resume music failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private static void stop(MediaPlayer mediaPlayer, Context context) {
        // Your stop logic here
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
//        Toast.makeText(context, "Stop music", Toast.LENGTH_SHORT).show();
    }

    private static class PrepareMusicTask extends AsyncTask<String, Void, Boolean> {
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private final MediaPlayer mediaPlayer;

        public PrepareMusicTask(Context context, MediaPlayer mediaPlayer) {
            this.context = context;
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        protected Boolean doInBackground(String... songLinks) {

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, Uri.parse(songLinks[0]));
                return  true;
            } catch (IllegalArgumentException | SecurityException | IllegalStateException |
                     IOException e) {
                e.printStackTrace();
            }
            return  false;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                try {
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            mp.start();
                        }
                    });
                    mediaPlayer.prepareAsync(); // Use asynchronous prepare
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    // Handle the exception
                    // Toast.makeText(context, "Failed to prepare music", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Toast.makeText(context, "Failed to prepare music", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
