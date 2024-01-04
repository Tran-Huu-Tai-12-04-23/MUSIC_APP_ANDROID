package com.example.musicplayer.activity.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityStudioUserBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Adapter.MusicItemUserUploadAdapter;
import DTO.ResponseData;
import Model.Song;
import Model.User;
import Service.ApiService;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Util;

public class StudioUser extends AppCompatActivity {
    private ActivityStudioUserBinding binding;
    private User user;
    private ArrayList<Song> songs;
    private MusicItemUserUploadAdapter musicItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudioUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        songs = new ArrayList<>();
        Bundle bundleGetData = getIntent().getBundleExtra("user");
        if( bundleGetData != null ) {
            this.user = (User)bundleGetData.getSerializable("user");
            initViewUSer();
            initSongByUser();
        }

        binding.btnUploadNewSong.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            Intent intent = new Intent(getApplicationContext(), UploadNewSong.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", this.user);
            intent.putExtra("user", bundle);
            startActivity(intent);
        }
        );

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        musicItemAdapter = new MusicItemUserUploadAdapter (this, songs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        binding.content.setLayoutManager(layoutManager);
        binding.content.setAdapter(musicItemAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.content);

        musicItemAdapter.setOnCheckedItem(this::changeScopeMode);

    }

    private void changeScopeMode(Song data) {
        if( data == null ) return;

        ApiService.ApiService.changeScope(data.getId(), !data.getPrivate()).enqueue(new Callback<Boolean>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( response.isSuccessful() ) {
                    songs.forEach(s -> {
                        if( s.getId() == data.getId() ) {
                            s.setPrivate(!s.getPrivate());
                        }
                    });
                    musicItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    private void initViewUSer() {
        if( this.user == null ) return;
        binding.nbFollower.setText(user.getTotalFollow() + "người theo dõi");
        Glide.with(getApplicationContext())
                .load(user.getAvatar())
                .placeholder(R.drawable.baseline_person_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(binding.avatar);
    }

    private void initSongByUser() {
        if( user == null ) return;

//        loadingDialog.startLoading();
        ApiService.ApiService.getAllSongByUser(user.getId()).enqueue(new Callback<ResponseData<List<Song>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Song>>> call, Response<ResponseData<List<Song>>> response) {
//                loadingDialog.stopLoading();
                if( response.isSuccessful()) {
                    ResponseData<List<Song>> res = response.body();
                    if( res == null ) return;
                    songs.clear();
                    songs.addAll(res.getData());
                    musicItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<Song>>> call, Throwable t) {
//                loadingDialog.stopLoading();
                Log.i("TAG", t.getLocalizedMessage());
            }
        });
    }
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Take action for the swiped item
        }
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int positionCom = viewHolder.getBindingAdapterPosition();
            if( songs.size() <=  positionCom ) return;
            Song song = songs.get(positionCom);
            if( song == null) return;

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isCurrentlyActive) {
                if (dX <= -500 ) {
                    removeSong(song);
                    super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
                    return;
                }
            }

            if (dX < 0 && dX >= -500 ) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_error))
                        .addActionIcon(R.drawable.trash_bin_2_svgrepo_com)
                        .create()
                        .decorate();
            } else if( dX < -500 ) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_success))
                        .addActionIcon(R.drawable.checked_svgrepo_com)
                        .create()
                        .decorate();

            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    };

    private void removeSong(Song song) {
        if( song == null ) return;
        ApiService.ApiService.removeSong(song.getId()).enqueue(new Callback<Boolean>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( response.isSuccessful() ) {
                    List<Song> filteredSongs = songs.stream()
                            .filter(s -> song.getId() != s.getId())
                            .collect(Collectors.toList());
                    songs.clear();
                    songs.addAll(filteredSongs);
                    musicItemAdapter.notifyDataSetChanged();
                    Snackbar.make(binding.getRoot(), "Xóa bài hát thành công!", Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undoSong(song);
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    private void undoSong(Song song) {
        if( song == null ) return;

        ApiService.ApiService.addSong(song).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                if( response.isSuccessful() ) {
                    initSongByUser();
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSongByUser();
    }
}