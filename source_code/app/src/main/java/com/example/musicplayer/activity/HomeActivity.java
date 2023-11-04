package com.example.musicplayer.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import com.example.musicplayer.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import adapter.ItemCarouselAdapter;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_home);

        RecyclerView musicSlide = findViewById(R.id.carousel_music_container);
        drawerLayout = findViewById(R.id.drawer_page);
        navigationView = findViewById(R.id.navigate_main);
        toolbar = findViewById(R.id.toolbar);

//        tool bar handle
           setSupportActionBar(toolbar);
//        navigation drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_active, R.string.navigation_de_active);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        ArrayList<String> listImg = new ArrayList<>();

        listImg.add("https://i.scdn.co/image/ab67616d0000b2734cf98acc86f655eb18104406");
        listImg.add("https://i.scdn.co/image/ab67616d00001e026f51023755dbf3e9db1215f5");
        listImg.add("https://i.scdn.co/image/ab67616d00001e02f9be0085f0de1ce792e1e7a1");
        listImg.add("https://i.scdn.co/image/ab67616d00001e02cf4be370976c2f6ef7deff8d");
        listImg.add("https://i.scdn.co/image/ab67616d0000b273eea60abbcaaf25c2880f11d5");
        listImg.add("https://i.scdn.co/image/ab67616d0000b2734cf98acc86f655eb18104406");
        listImg.add("https://i.scdn.co/image/ab67616d00001e026f51023755dbf3e9db1215f5");
        listImg.add("https://i.scdn.co/image/ab67616d00001e02f9be0085f0de1ce792e1e7a1");
        listImg.add("https://i.scdn.co/image/ab67616d00001e02cf4be370976c2f6ef7deff8d");
        listImg.add("https://i.scdn.co/image/ab67616d0000b273eea60abbcaaf25c2880f11d5");

        ItemCarouselAdapter itemCarouselAdapter = new ItemCarouselAdapter(getApplicationContext(), listImg);

        itemCarouselAdapter.setOnItemClickListener(new ItemCarouselAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, String url) {

            }
        });
        musicSlide.setAdapter(itemCarouselAdapter);

    }


}