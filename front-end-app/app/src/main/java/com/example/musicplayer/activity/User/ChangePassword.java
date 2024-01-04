package com.example.musicplayer.activity.User;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityChangePassword2Binding;
import com.google.android.material.snackbar.Snackbar;

import DTO.ResponseData;
import DTO.UserChangePasswordRequest;
import Model.User;
import Service.ApiService;
import io.github.muddz.styleabletoast.StyleableToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;
import utils.Util;

public class ChangePassword extends AppCompatActivity {

    private ActivityChangePassword2Binding binding;
    private User user;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePassword2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = new LoadingDialog(this);

        Bundle bundle = getIntent().getBundleExtra("user");

        if( bundle != null ) {
            this.user = (User)bundle.getSerializable("user");
        }

        binding.btnSave.setOnClickListener(v -> {
            if( this.user == null ) return;
            Util.applyClickAnimation(v);
            changePassword();
        });

        binding.btnBack.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            finish();
        });

    }

    private void changePassword() {
        String passwordOld = binding.inputPasswordOld.getText().toString().trim();
        String password = binding.inputPasswordNew.getText().toString().trim();
        String confirmPassword = binding.inputConfirmPasswordNew.getText().toString().trim();

        if( password.equals("") || passwordOld.equals("") || confirmPassword.equals("")) {
            Snackbar.make(binding.getRoot(), "Vui lòng nhập đầy đủ thông tin ", Snackbar.LENGTH_LONG).show();
            return;
        }else if( !password.equals(confirmPassword)) {
            Snackbar.make(binding.getRoot(), "Xác nhận mật khẩu không khớp!", Snackbar.LENGTH_LONG).show();
            return;
        }

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest();
        userChangePasswordRequest.setUserId(user.getId());
        userChangePasswordRequest.setOldPassword(passwordOld);
        userChangePasswordRequest.setPassword(password);
        userChangePasswordRequest.setConfirmPassword(confirmPassword);


        loadingDialog.startLoading();

        ApiService.ApiService.changePasswordByUSer(userChangePasswordRequest).enqueue(new Callback<ResponseData<User>>() {
            @Override
            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                loadingDialog.stopLoading();

                if( response.isSuccessful() ) {
                    ResponseData<User> res = response.body();
                    if( res == null ) return;
                    if( (res.getData() == null ) ) {
                        StyleableToast.makeText(getApplicationContext(), res.getMessage(), Toast.LENGTH_SHORT, R.style.toast_error).show();
                        return;
                    }
                    StyleableToast.makeText(getApplicationContext(), "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT, R.style.toast_success).show();
                    finish();
                }else {
                    StyleableToast.makeText(getApplicationContext(), "Thay đổi mật khẩu thất bại!", Toast.LENGTH_SHORT, R.style.toast_error).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                loadingDialog.stopLoading();
                StyleableToast.makeText(getApplicationContext(), "Thay đổi mật khẩu thất bại!", Toast.LENGTH_SHORT, R.style.toast_error).show();

            }
        });


    }
}
