package com.example.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.example.musicplayer.R;

import constanst.Constanst;
import fragment.startApp.IntroFragment;
import fragment.startApp.TransactionStart;

public class IntroActivity extends AppCompatActivity {

    private IntroFragment introFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        handle theme

        // Get the user's theme choice from SharedPreferences
        int systemDefaultTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        SharedPreferences preferences = getSharedPreferences("Theme", MODE_PRIVATE);
        int selectedTheme = preferences.getInt("mode", systemDefaultTheme);

        AppCompatDelegate.setDefaultNightMode(selectedTheme);


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_intro);

        introFragment = new IntroFragment();
        TransactionStart transactionFrag = new TransactionStart();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.mainLayoutStartScreen, transactionFrag, Constanst.TAG_FRAGMENT_TRANSACTION);
        fragmentTransaction.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("LoginData", MODE_PRIVATE);
                boolean isLoggedIn = preferences.getBoolean("loggedIn", false);
                if (isLoggedIn) {
                    Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.mainLayoutStartScreen, introFragment, Constanst.TAG_FRAGMENT_INTRO);
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.commit();
                }

            }
        }, 3000);
    }
}
