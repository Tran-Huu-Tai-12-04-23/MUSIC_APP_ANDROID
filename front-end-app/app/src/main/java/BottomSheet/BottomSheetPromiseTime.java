package BottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.BottomSheetPromiseTimeBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import Service.PlayMusicService;
import utils.Util;

public class BottomSheetPromiseTime extends BottomSheetDialogFragment  {
    private BottomSheetPromiseTimeBinding binding;
    private BottomSheetBehavior<View> bBehavior;
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = BottomSheetPromiseTimeBinding.inflate( getLayoutInflater());
        View view = binding.getRoot();

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());
        bBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        binding.btnTime1hour.setOnClickListener(v -> {
            PlayMusicService.startTimer(requireContext(), 3600000, view);
        });
        binding.btnTime45minutes.setOnClickListener(v -> {
            PlayMusicService.startTimer(requireContext(), 2700000, view);
        });
        binding.btnTime30minutes.setOnClickListener(v -> {
            PlayMusicService.startTimer(requireContext(), 1800000, view);
        });
        binding.btnTime15minutes.setOnClickListener(v -> {
            PlayMusicService.startTimer(requireContext(), 900000, view);
        });

        binding.btnModeSleep.setOnClickListener(v -> {
            PlayMusicService.startSleepMode(requireContext(), 300000, view);
        });

        binding.btnCancelTimer.setOnClickListener(v -> {
            PlayMusicService.cancelTimer(requireContext(), view);
        });


        updateUI();
        return dialog;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        if( requireActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            if( homeActivity.getTimerCount() == -1 ) {
                binding.btnCancelTimer.setVisibility(View.GONE);
                binding.title.setText("Hẹn giờ tắt nhạc");
            }else {
                binding.btnCancelTimer.setVisibility(View.VISIBLE);
                binding.title.setText("Nhạc tắt sau " + Util.convertDurationToString(homeActivity.getTimerCount()/1000));
            }
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
