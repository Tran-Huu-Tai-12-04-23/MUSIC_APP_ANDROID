package com.example.musicplayer.activity.ForGotPassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.google.android.material.button.MaterialButton;

import Model.User;
import DTO.ChangePasswordRequest;
import DTO.ResponseData;
import Service.ApiService;
import io.github.muddz.styleabletoast.StyleableToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

public class ChangePassword extends AppCompatActivity {

    MaterialButton btnExit;
    AppCompatButton btnSavePassword;
    EditText inputPassword, inputConfirmPassword;
    String phoneNumber = "";
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        loadingDialog = new LoadingDialog(this);
        phoneNumber = getIntent().getStringExtra("phone");

        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSavePassword = findViewById(R.id.btn_save_password);

        btnExit = findViewById(R.id.btn_exit);

        btnExit.setOnClickListener(v -> {
            this.backIntro();
        });

        btnSavePassword.setOnClickListener(v -> {

            String password = inputPassword.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            if( password.equals("") || confirmPassword.equals("")) {
                StyleableToast.makeText(getApplicationContext(), "Vui lòng điền mật khẩu!", Toast.LENGTH_LONG, R.style.toast_warn).show();
                return;
            }else if( !password.equals(confirmPassword)) {
                StyleableToast.makeText(getApplicationContext(), "Xác nhận mật khẩu không khớp!", Toast.LENGTH_LONG, R.style.toast_error).show();
                return;
            }

            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setPassword(password);
            changePasswordRequest.setConfirmPassword(confirmPassword);
            changePasswordRequest.setUserPhone("0"+phoneNumber);

            changePassword(changePasswordRequest);
        });

    }


    private void changePassword(ChangePasswordRequest changePasswordRequest) {
        loadingDialog.startLoading();
        ApiService.ApiService.resetPassword(changePasswordRequest).enqueue(new Callback<ResponseData<User>>() {
            @Override
            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                loadingDialog.startLoading();
                if( !response.isSuccessful()){
                    StyleableToast.makeText(getApplicationContext(), "Thay đổi mật khẩu không thành công", Toast.LENGTH_LONG, R.style.toast_error).show();
                    return;
                }
                StyleableToast.makeText(getApplicationContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_LONG, R.style.toast_success).show();

                loadingDialog.startLoading();
                StyleableToast.makeText(getApplicationContext(), "Thay đổi mật khẩu thành công!", Toast.LENGTH_LONG, R.style.toast_success).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backIntro();
                    }
                }, 1000);


            }

            @Override
            public void onFailure(Call<ResponseData<User>> call, Throwable t) {

            }
        });
    }

    void backIntro() {
        finish();
    }


    @Override
    protected void onDestroy() {
        loadingDialog.destroyDialog();
        super.onDestroy();
    }
}