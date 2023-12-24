package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.musicplayer.R;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Model.Song;

public class Util  {

    @SuppressLint("DefaultLocale")
    public static String convertToShortForm(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1000000) {
            return String.format("%.1fk", number / 1000.0);
        } else {
            return String.format("%.1fm", number / 1000000.0);
        }
    }

    public static void switchFragment(FragmentTransaction transactionFG, Fragment fragment,boolean isHasNotBack, String tag) {

        if( tag.trim().equals("")) {
            transactionFG.replace(R.id.mainLayoutStartScreen, fragment, tag);
        }else {
            transactionFG.replace(R.id.mainLayoutStartScreen, fragment);
        }
        if( isHasNotBack ==  false ) {
            transactionFG.addToBackStack(null);
        }

        transactionFG.commit();
    }

    public static boolean isValidVietnamesePhoneNumber(String phoneNumber) {
        // Define the regular expression pattern for a Vietnamese phone number
        String regex = "^(0[1-9][0-9]{8}|\\+84[1-9][0-9]{8})$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(phoneNumber);

        // Check if the phone number matches the pattern
        return matcher.matches();


    }

    public static String convertDurationToString(long durationInMillis) {
        long hours = durationInMillis / 3600;
        long minutes = (durationInMillis % 3600) / 60;
        long seconds = durationInMillis % 60;

        String hoursStr = hours > 0 ? String.format("%02d", hours) + ":" : "";
        String minutesStr = String.format("%02d", minutes);
        String secondsStr = String.format("%02d", seconds);

        return hoursStr + minutesStr + ":" + secondsStr;
    }
    public static void switchTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int currentThemeMode = preferences.getInt("mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        if (currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putInt("mode", AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putInt("mode", AppCompatDelegate.MODE_NIGHT_YES);
        }

        editor.apply();

    }

    // dark = true , light = false
    public static boolean isDarkModeEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Theme", Context.MODE_PRIVATE);
        int currentThemeMode = preferences.getInt("mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        return currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES;
    }


    public static void applyClickAnimation(View view) {
        // ScaleAnimation to create a zoom-in effect
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, 0.8f,
                1f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(200); // Set the duration of the animation
        scaleAnimation.setRepeatMode(Animation.REVERSE); // Optional: make it bounce back

        // Start the animation
        view.startAnimation(scaleAnimation);
    }

    public static void applyClickAnimationFlash(View view) {
        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(view);
    }

    public static ColorStateList getIconActiveColor(Context context) {
        return ColorStateList.valueOf(ContextCompat.getColor(context, R.color.icon_primary));
    }

    public static ColorStateList getIconDeactiveColor(Context context) {
        return ColorStateList.valueOf(ContextCompat.getColor(context, R.color.icon_second));
    }


    public static Color getTextActiveColor(Context context) {
        return Color.valueOf(ContextCompat.getColor(context, R.color.icon_primary));
    }

    public static Color getTextDeactiveColor(Context context) {
        return Color.valueOf(ContextCompat.getColor(context, R.color.icon_second));
    }

    public static int getPrevSong(Song currentSong, List<Song> songs) {
        if (songs == null) return -1;
        int indexSong = -1;
        int size = songs.size();
        if (size <= 1) {
            return -1;
        }
        for (int index = 0; index < size; index++) {
            Song song = songs.get(index);
            if (song.getId() == currentSong.getId()) {
                if (index - 1 >= 0) {
                    indexSong = index - 1;
                } else {
                    indexSong = size - 1;
                }
                break;
            }
        }
        if( indexSong == -1  ) {
            indexSong = 0;
        }
        return indexSong;
    }

    public static int getNextSong(Song currentSong, List<Song> songs) {
        if( songs == null) return -1;
        int indexSong = -1;
        int size = songs.size();

        if (size <= 1) {
            return -1;
        }

        for (int index = 0; index < size; index++) {
            Song song = songs.get(index);
            if (song.getId() == currentSong.getId()) {
                if( index + 1 <= size - 1) {
                    indexSong = index + 1;
                }else {
                    indexSong = 0;
                }
                break;
            }
        }

        if( indexSong == -1  ) {
            indexSong = 0;
        }

        return indexSong;
    }


    public static int randomIndexSong(Song currentSong, List<Song> songs) {
        if( songs == null) return -1;
        Random random = new Random();
        int randomIndex;
        int size = songs.size();
        if (size <= 1) {
            return -1;
        }
        do {
            randomIndex = random.nextInt(size);
        } while (songs.get(randomIndex).getId() == currentSong.getId());

        return randomIndex;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
