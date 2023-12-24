package com.example.musicplayer.activity.User;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityEditProfileBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Date;

import BottomSheet.BottomSheetChooseTypeUploadAvatar;
import Firebase.FirebaseService;
import Interface.BottomSheetChoosePictureInteractionListener;
import Interface.OnImageUploadCompleteListener;
import Model.ProfileDT;
import Model.User;
import Service.ApiService;
import io.github.muddz.styleabletoast.StyleableToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;
import utils.Util;

public class EditProfile extends AppCompatActivity implements BottomSheetChoosePictureInteractionListener {
    private ActivityEditProfileBinding binding;
    private User user;
    private Date dateOfBirth;
    private String avatar;
    private LoadingDialog loadingDialog;
    private ProfileDT profileDT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = new LoadingDialog(this);

        Bundle bundle = getIntent().getBundleExtra("user");
        if( bundle != null ) {
            this.user = (User)bundle.getSerializable("user");
            initProfile(user);
        }


        binding.btnSelectDate.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select dates")
                            .build();
            datePicker.show(getSupportFragmentManager(), "Pick date");
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    dateOfBirth = new Date(selection);
                    binding.btnSelectDate.setText(dateOfBirth.toLocaleString());
                }
            });
        });

        binding.btnSave.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            boolean verifyData = verifyData();
            if(!verifyData) return;

            saveProfile();
        });

        binding.btnEditAvatar.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            BottomSheetChooseTypeUploadAvatar bottomSheetChooseTypeUploadAvatar = new BottomSheetChooseTypeUploadAvatar();
            bottomSheetChooseTypeUploadAvatar.show(getSupportFragmentManager(), "Open option avatar");
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });



    }


    private boolean verifyData() {
        if( dateOfBirth == null || binding.inputDes.getText().toString().equals("") || binding.inputAddress.getText().toString().equals("")
            || binding.inputFullName.getText().toString().equals("")
        ) {
            StyleableToast.makeText(getApplicationContext(), "Vui lòng nhập đẩy đủ thông tin!", Toast.LENGTH_SHORT, R.style.toast_warn).show();
            return false;
        }
        return true;
    }

    private void handleUpdateUI() {
        if(profileDT == null ) return;
        this.avatar = profileDT.getUser().getAvatar();
        Glide.with(getApplicationContext())
                .load(avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.avatar);

        binding.inputFullName.setText(profileDT.getFullName());
        binding.inputAddress.setText(profileDT.getAddress());
        binding.inputDes.setText(profileDT.getDescription());
        this.dateOfBirth = profileDT.getDateOfBirth();
        binding.btnSelectDate.setText(this.dateOfBirth.toLocaleString());

    }

    private void initProfile(User user) {
        if( user == null ) return;
        loadingDialog.startLoading();

        ApiService.ApiService.getProfileByUser(user.getId()).enqueue(new Callback<ProfileDT>() {
            @Override
            public void onResponse(Call<ProfileDT> call, Response<ProfileDT> response) {
                loadingDialog.stopLoading();
                if( response.isSuccessful() ) {
                    profileDT = response.body();
                    handleUpdateUI();
                }
            }

            @Override
            public void onFailure(Call<ProfileDT> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("TAG", t.getLocalizedMessage());
            }

        });

    }

    private void saveProfile() {
        if( user == null ) return;
        if( profileDT == null ) profileDT = new ProfileDT();
        user.setAvatar(this.avatar);
        profileDT.setUser(user);
        profileDT.setFullName(binding.inputFullName.getText().toString());
        profileDT.setAddress(binding.inputAddress.getText().toString());
        profileDT.setDescription(binding.inputDes.getText().toString());
        profileDT.setDateOfBirth(this.dateOfBirth);

        loadingDialog.startLoading();
        ApiService.ApiService.editProfile(profileDT).enqueue(new Callback<ProfileDT>() {
            @Override
            public void onResponse(Call<ProfileDT> call, Response<ProfileDT> response) {
                loadingDialog.stopLoading();
                if( response.isSuccessful() ) {
                    StyleableToast.makeText(getApplicationContext(), "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT, R.style.toast_success).show();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ProfileDT> call, Throwable t) {
                StyleableToast.makeText(getApplicationContext(), "Cập nhật hồ sơ thất bại!", Toast.LENGTH_SHORT, R.style.toast_error).show();
                loadingDialog.stopLoading();

            }
        });
    }

    private void updateAvatar(String imgUrl) {
        this.avatar = imgUrl;
        Glide.with(getApplicationContext())
                .load(imgUrl)
                .placeholder(R.drawable.baseline_person_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.avatar);
    }


    @Override
    public void onChooseImg(Uri img) {
        loadingDialog.startLoading();
        FirebaseService firebaseService = new FirebaseService(getApplicationContext());
        firebaseService.uploadImage(img, new OnImageUploadCompleteListener() {
            @Override
            public void onImageUploadComplete(@Nullable String imageUrl) {
                updateAvatar(imageUrl);
                loadingDialog.stopLoading();

            }
        });
    }


}