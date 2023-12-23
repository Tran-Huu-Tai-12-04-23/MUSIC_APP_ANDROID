package Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import Constanst.Constant;
import Model.Song;
import Service.PlayMusicService;

public class PlayMusicReceiver extends BroadcastReceiver {
    private static final String TAG = "SongReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Constant.ACTION_MUSIC)) {
            // handle action music
            int action = intent.getIntExtra(Constant.ACTION_TYPE, 0);
            if (action != 0) {
                Intent serviceIntent = new Intent(context, PlayMusicService.class);
                serviceIntent.putExtra(Constant.ACTION_TYPE, action);
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Song song = (Song) bundle.getSerializable(Constant.CURRENT_SONG);
                    if (song != null) {
                        Log.e(TAG, "onReceive: receive current song : " + song.toString());
                        serviceIntent.putExtra(Constant.CURRENT_SONG, song);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }

        }

    }
}
