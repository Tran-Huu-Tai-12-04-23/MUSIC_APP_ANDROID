package com.example.musicplayer.activity.ForGotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.musicplayer.R;
import com.google.firebase.FirebaseApp;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}