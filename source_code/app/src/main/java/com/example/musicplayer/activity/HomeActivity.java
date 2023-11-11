package com.example.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import adapter.CommentListAdapter;
import adapter.HandleListeningItemClicked;
import adapter.MusicItemAdapter;
import data.FakeData;
import fragment.mainApp.*;
import fragment.mainApp.ContentHomePage;
//import fragment.mainApp.searchPage.Search;
//import fragment.mainApp.searchPage.SearchToolbar;
import fragment.searchPage.Search;
import fragment.searchPage.SearchToolbar;
import utils.Util;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView sidebar;
    private Toolbar toolbar;
    private ConstraintLayout loadingHome;
    private BottomNavigationView bottomNavigate;
    private LinearLayout floatingPayer;
    private boolean isShowDialogPlayMusic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_page);
        sidebar = findViewById(R.id.navigate_main);
        toolbar = findViewById(R.id.toolbar);
        loadingHome = findViewById(R.id.loading_home);
        bottomNavigate = findViewById(R.id.bottom_navigation);
        floatingPayer = findViewById(R.id.floating_player);

//        var playmusic dialog

        loadingHome.setVisibility(View.GONE);

//        tool bar handle/*/
        setSupportActionBar(toolbar);
        sidebar.bringToFront();
//        navigation drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_active, R.string.navigation_de_active);
        drawerLayout.addDrawerListener(toggle);

//      handle click item
        sidebar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_playlist) {
                    Toast.makeText(HomeActivity.this, "Hello playlist", Toast.LENGTH_SHORT).show();
                }else if( itemId == R.id.nav_logout) {
                    logout();
                }else if(itemId == R.id.switch_theme_custom) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadingHome.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Util.switchTheme(getApplicationContext());
                            loadingHome.setVisibility(View.GONE);
                            reloadApp();
                        }
                    }, 1000);

                }
                // Add more "if" conditions for other menu items here
                return false;
            }
        });
//        end handle click item


        toggle.syncState();


        bottomNavigate.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int idItem = item.getItemId();
                if( idItem == R.id.nav_home) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    MainToolbar mainToolbar = new MainToolbar();
                    ContentHomePage contentHomePage = new ContentHomePage();
                    toolbar.setVisibility(View.VISIBLE);
                    fragmentTransaction.replace(R.id.main_toolbar_frag, mainToolbar);
                    fragmentTransaction.replace(R.id.content_home_page, contentHomePage);
                    fragmentTransaction.commit();
                }else if( idItem == R.id.nav_library) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    SearchToolbar searchToolbar = new SearchToolbar();
                    LibraryScreen libraryScreen = new LibraryScreen();
                    fragmentTransaction.replace(R.id.main_toolbar_frag, searchToolbar);
                    toolbar.setVisibility(View.GONE);
                    fragmentTransaction.replace(R.id.content_home_page, libraryScreen);
                    fragmentTransaction.commit();
                }else if( idItem == R.id.nav_search){
                    toolbar.setVisibility(View.VISIBLE);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    SearchToolbar searchToolbar = new SearchToolbar();
                    Search mainScreenSearch = new Search();
                    fragmentTransaction.replace(R.id.main_toolbar_frag, searchToolbar);
                    fragmentTransaction.replace(R.id.content_home_page, mainScreenSearch);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });

        View rootView = getWindow().getDecorView().getRootView();

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    bottomNavigate.setVisibility(View.GONE);
                } else {
                    bottomNavigate.setVisibility(View.VISIBLE);
                }
            }
        });

//        open fragment player when click floating player
        floatingPayer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if(!isShowDialogPlayMusic)
                {
                    isShowDialogPlayMusic  = true;
                    openPlayMusicDialog();
                }
            }
        });
    }

    private void openPlayMusicDialog() {
        ShapeableImageView btnPrev, btnPlay, btnPause, btnNext ;
        MaterialButton btnClosePlayMusic, btnOpenMenuMusic, btnWatchDetailSong, btnOpenMusicComment;


        Dialog dialogPlayMusic = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialogPlayMusic.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPlayMusic.setContentView(R.layout.play_music_dialog);

        Window window = dialogPlayMusic.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialogPlayMusic.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogPlayMusic.setCanceledOnTouchOutside(true);
        dialogPlayMusic.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialogPlayMusic.setOnDismissListener(dialogInterface -> {
            isShowDialogPlayMusic = false;
        });
//        hadnle button in dialog
        btnNext = dialogPlayMusic.findViewById(R.id.btn_next_music);
        btnPlay = dialogPlayMusic.findViewById(R.id.btn_start_music);
        btnPause = dialogPlayMusic.findViewById(R.id.btn_pause_music);
        btnPrev = dialogPlayMusic.findViewById(R.id.btn_prev_music);
        btnClosePlayMusic = dialogPlayMusic.findViewById(R.id.btn_close_play_music);
        btnOpenMenuMusic = dialogPlayMusic.findViewById(R.id.btn_open_menu_music);
        btnWatchDetailSong = dialogPlayMusic.findViewById(R.id.btn_detail_song);
        btnOpenMusicComment = dialogPlayMusic.findViewById(R.id.btn_open_comment_music);

        btnOpenMusicComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentDialog();
            }
        });

        btnWatchDetailSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModalDetailSong();
            }
        });

        btnClosePlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPlayMusic.dismiss();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickAnimation(v);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickAnimation(v);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickAnimation(v);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickAnimation(v);
            }
        });
        dialogPlayMusic.show();
    }
    private void  openCommentDialog() {
        MaterialButton btnCloseComment;
        RecyclerView commentListDataRecycleView ;

        Dialog dialogComment = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialogComment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogComment.setContentView(R.layout.comment_dialog);

        btnCloseComment = dialogComment.findViewById(R.id.btn_close_comment);

        Window window = dialogComment.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialogComment.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogComment.setCanceledOnTouchOutside(true);
        dialogComment.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

//        handle onclick btn in dialog
        btnCloseComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogComment.dismiss();
            }
        });

//        render data recycle view
        commentListDataRecycleView  = dialogComment.findViewById(R.id.container_comment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        CommentListAdapter commentListAdapter = new CommentListAdapter(getApplicationContext(), FakeData.getListImg());

        commentListAdapter.setOnItemClickListener(new HandleListeningItemClicked() {
            @Override
            public void onClick(ImageView imageView, String url) {

            }
        });

        commentListDataRecycleView.setLayoutManager(layoutManager);
        commentListDataRecycleView.setAdapter(commentListAdapter);

        dialogComment.show();
    }

    private void openModalDetailSong() {
        MaterialButton btnCloseDialogDetailSong;

        MaterialAlertDialogBuilder dialogBuilderDetailSong = new MaterialAlertDialogBuilder(HomeActivity.this);
        View customLayout = getLayoutInflater().inflate(R.layout.detail_song, null);
        dialogBuilderDetailSong.setView(customLayout);
        GradientDrawable borderDrawable = (GradientDrawable) ContextCompat.getDrawable(HomeActivity.this, R.drawable.view_rounded);
        int bgColor = ContextCompat.getColor(HomeActivity.this, R.color.bg_navigate);
        if(borderDrawable != null ) {
            borderDrawable.setColor(bgColor);
        }
        dialogBuilderDetailSong.setBackground(borderDrawable);
//                set variable
        btnCloseDialogDetailSong = customLayout.findViewById(R.id.btn_close_dialog_song_detail);

        Dialog dialog = dialogBuilderDetailSong.create();
        btnCloseDialogDetailSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBuilderDetailSong.setCancelable(true);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if( drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void logout() {
        // Update SharedPreferences to mark the user as not logged in
        SharedPreferences preferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();

        // Navigate to the login screen or perform any other necessary logout actions
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish(); // Close the current activity if needed
    }


    private void reloadApp() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    public void onInputFocusChanged(boolean hasFocus) {

        Intent intent = new Intent("IS_FOCUS");
        intent.putExtra("FOCUS_SEARCH", hasFocus);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void applyClickAnimation(View view) {
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

}