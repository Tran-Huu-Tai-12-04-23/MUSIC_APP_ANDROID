package BottomSheet;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;

import Constanst.Constant;
import Model.Song;
import utils.Util;

public class BottomSheetSwitchTheme extends BottomSheetDialogFragment {
    private BottomSheetBehavior<View> bBehavior;
    private boolean currentTheme;
    MaterialButton btnActiveDarkTheme, btnActiveLightTheme;
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        currentTheme = Util.isDarkModeEnabled(requireContext());

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_switch_theme, null);
        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());
        bBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        btnActiveDarkTheme = view.findViewById(R.id.btnActiveDarkTheme);
        btnActiveLightTheme = view.findViewById(R.id.btnActiveLightTheme);


        btnActiveDarkTheme.setOnClickListener(v -> {
            if( !currentTheme)  Util.switchTheme(requireContext());
            dialog.dismiss();
        });

        btnActiveLightTheme.setOnClickListener(v -> {
            if( currentTheme) Util.switchTheme(requireContext());
            dialog.dismiss();
        });

        changeColorBtnThemeAction(this.currentTheme);


        return dialog;
    }

    private void changeColorBtnThemeAction(boolean currentTheme) {
        ColorStateList iconActiveColor, iconDeactiveColor, textActiveColor, textDeactiveColor;

        // Use ContextCompat.getColorStateList for backward compatibility
        iconActiveColor = Util.getIconActiveColor(getContext());
        iconDeactiveColor = Util.getIconDeactiveColor(getContext());
        textActiveColor = Util.getIconActiveColor(getContext());
        textDeactiveColor = Util.getIconDeactiveColor(getContext());

        if (currentTheme) {
            // dark mode
            btnActiveLightTheme.setIconTint(iconDeactiveColor);
            btnActiveLightTheme.setTextColor(textDeactiveColor);
            btnActiveDarkTheme.setIconTint(iconActiveColor);
            btnActiveDarkTheme.setTextColor(textActiveColor);
        } else {
            // light mode
            btnActiveLightTheme.setIconTint(iconActiveColor);
            btnActiveLightTheme.setTextColor(textActiveColor);
            btnActiveDarkTheme.setIconTint(iconDeactiveColor);
            btnActiveDarkTheme.setTextColor(textDeactiveColor);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
