package com.example.musicplayer.activity.User;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityUploadNewSongBinding;

import java.io.IOException;
import java.util.Date;

import BottomSheet.BottomSheetChooseTypeUploadAvatar;
import Firebase.FirebaseService;
import Interface.BottomSheetChoosePictureInteractionListener;
import Interface.OnAudioUploadCompleteListener;
import Interface.OnImageUploadCompleteListener;
import Model.Song;
import Model.User;
import Service.ApiService;
import io.github.muddz.styleabletoast.StyleableToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;
import utils.Util;

public class UploadNewSong extends AppCompatActivity implements BottomSheetChoosePictureInteractionListener {
    private static final int PICK_AUDIO_REQUEST_CODE = 100;
    private ActivityUploadNewSongBinding binding;
    private User user;
    private String audioLink;
    private String thumbnails;
    private LoadingDialog loadingDialog;
    private int duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(this);
        binding = ActivityUploadNewSongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getBundleExtra("user");
        if( bundle != null ) {
            this.user = (User)bundle.getSerializable("user");
        }

        binding.btnBack.setOnClickListener(v -> finish());


        binding.btnAddThumbnails.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            BottomSheetChooseTypeUploadAvatar bottomSheetChooseTypeUploadAvatar = new BottomSheetChooseTypeUploadAvatar();
            bottomSheetChooseTypeUploadAvatar.show(getSupportFragmentManager(), "open choose thumbnails");
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSelectSong.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            selectAudio();
        });

        binding.btnSave.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            addSong();
        });
    }

    private void addSong() {
        if( user == null ) return;
        String nameSong = binding.inputNameSong.getText().toString();
        String gener = binding.inputGenre.getText().toString();
        boolean isPrivate = binding.selectMode.isChecked();

        if( nameSong.equals("") || gener.equals("") || thumbnails.equals("") || audioLink.equals("")) {
            StyleableToast.makeText(getApplicationContext(), "Vui lòng nhập đẩy đủ thông tin!", Toast.LENGTH_SHORT, R.style.toast_warn).show();
            return;
        }


        Toast.makeText(this, String.valueOf(isPrivate), Toast.LENGTH_SHORT).show();
        Song song = new Song();
        song.setUserUpload(user);
        song.setTitle(nameSong);
        song.setGenre(gener);
        song.setThumbnails(this.thumbnails);
        song.setSongLink(this.audioLink);
        song.setUploadDate(new Date());
        song.setDuration(this.duration);
        song.setPrivate(isPrivate);


        loadingDialog.startLoading();
        ApiService.ApiService.addSong(song).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                loadingDialog.stopLoading();
                if( response.isSuccessful() ) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("TAG", t.getMessage());

            }
        });


    }
    private int getAudioDuration(Uri audioUri) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(getApplicationContext(), audioUri);
            mediaPlayer.prepare();
            return mediaPlayer.getDuration() / 1000;
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        } finally {
            mediaPlayer.release();
        }

        return 0; // Return 0 in case of an error
    }

    private void selectAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Lấy đường dẫn của file âm thanh được chọn
            Uri selectedAudioUri = data.getData();
            this.duration = getAudioDuration(selectedAudioUri);
            uploadAudio(selectedAudioUri);
        }
    }

    private void uploadAudio(Uri fileAU) {
        loadingDialog.startLoading();
        FirebaseService firebaseService = new FirebaseService(getApplicationContext());
        firebaseService.uploadAudio(fileAU, new OnAudioUploadCompleteListener() {
            @Override
            public void onUploadCompleteAudio(@Nullable String audioURL) {
                loadingDialog.stopLoading();

                audioLink = audioURL;
                binding.btnSelectSong.setText(audioURL);
            }
        });

    }

    @Override
    public void onChooseImg(Uri img) {
        loadingDialog.startLoading();
        FirebaseService firebaseService = new FirebaseService(getApplicationContext());
        firebaseService.uploadImage(img, new OnImageUploadCompleteListener() {
            @Override
            public void onImageUploadComplete(@Nullable String imageUrl) {
                thumbnails = imageUrl;
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.baseline_person_24)
                        .into(binding.thumbnails);

                loadingDialog.stopLoading();

            }
        });
    }
}