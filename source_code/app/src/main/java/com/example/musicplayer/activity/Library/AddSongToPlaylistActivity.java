package com.example.musicplayer.activity.Library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.databinding.ActivityAddSongToPlaylistBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.PlaylistAddSongAdapter;
import BottomSheet.BottomSheetAddPlaylist;
import DTO.DetailPlaylistRequest;
import DTO.ResponseData;
import Interface.BottomSheetAddPlaylistInteractionListener;
import Interface.HandleCheckedItem;
import Interface.HandleListeningItemClicked;
import Model.Playlist;
import Model.User;
import Service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

public class AddSongToPlaylistActivity extends AppCompatActivity implements BottomSheetAddPlaylistInteractionListener {


    private ActivityAddSongToPlaylistBinding binding;
    private Long userId;
    private Long songId;
    private PlaylistAddSongAdapter playlistAddSongAdapter;
    private ArrayList<Playlist> playlists;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSongToPlaylistBinding.inflate(getLayoutInflater());
        loadingDialog = new LoadingDialog(this);
        playlists = new ArrayList<>();
        // Retrieve data from Intent
        songId = getIntent().getLongExtra("songId", -1);
        userId = getIntent().getLongExtra("userId", -1);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        playlistAddSongAdapter = new PlaylistAddSongAdapter(getApplicationContext(),  playlists);
        binding.containerPlaylistItem.setLayoutManager(layoutManager);
        binding.containerPlaylistItem.setAdapter(playlistAddSongAdapter);

        binding.btnNewPlaylist.setOnClickListener(v -> {
            BottomSheetAddPlaylist bottomSheetAddPlaylist = new BottomSheetAddPlaylist();
            User user = new User();
            user.setId(userId);
            bottomSheetAddPlaylist.setUser(user);
            bottomSheetAddPlaylist.show(getSupportFragmentManager(), "Add new playlist");
        });

        playlistAddSongAdapter.setOnItemClickListener(new HandleListeningItemClicked<Playlist>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(Playlist data) {
                playlists.stream().forEach(pl -> {
                    if(pl.getId().equals(data.getId())) {
                        pl.setSelected(!pl.getSelected());
                    }
                });
                playlistAddSongAdapter.notifyDataSetChanged();
            }
        });
        playlistAddSongAdapter.setOnCheckedItem(new HandleCheckedItem<Playlist>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChecked(Playlist data) {
                playlists.stream().forEach(pl -> {
                    if(pl.getId().equals(data.getId())) {
                        pl.setSelected(!pl.getSelected());
                    }
                });
                playlistAddSongAdapter.notifyDataSetChanged();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.btnDone.setOnClickListener(v -> {
            doneAddSongToPlaylist();
        });

        initPlaylistNotExistSong();
        setContentView(binding.getRoot());
    }

    private void doneAddSongToPlaylist() {
        loadingDialog.startLoading();
        for(Playlist playlist : playlists ) {
            if(playlist.getSelected()) {
                addSongIntoPlaylist(playlist);
            }
        }

        loadingDialog.stopLoading();

        finish();
    }

    private void addSongIntoPlaylist(Playlist playlist) {
        DetailPlaylistRequest detailPlaylistRequest = new DetailPlaylistRequest();
        detailPlaylistRequest.setSongId(songId);
        detailPlaylistRequest.setPlaylistId(playlist.getId());

        ApiService.ApiService.addSongIntoPlaylist(detailPlaylistRequest).enqueue(new Callback<ResponseData<Playlist>>() {
            @Override
            public void onResponse(Call<ResponseData<Playlist>> call, Response<ResponseData<Playlist>> response) {
                if( response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<ResponseData<Playlist>> call, Throwable t) {
                Log.i("add song into playlist", t.getLocalizedMessage());
            }
        });
    }

    private void initPlaylistNotExistSong() {
        if( songId == -1 || userId == -1) return;
        loadingDialog.startLoading();
        ApiService.ApiService.getAllPlaylistByUserNotExistSong(userId, songId).enqueue(new Callback<ResponseData<List<Playlist>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Playlist>>> call, Response<ResponseData<List<Playlist>>> response) {
                loadingDialog.stopLoading();

                if( response.isSuccessful()) {
                    assert response.body() != null;
                    List<Playlist> resPlaylists = response.body().getData();
                    playlists.clear();
                    playlists.addAll(resPlaylists);
                    playlistAddSongAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<ResponseData<List<Playlist>>> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("Add song to playlist", t.getLocalizedMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChangeAddPlaylist(Playlist playlist) {
        if( playlist == null ) return;
        playlist.setCountSong(0L);
        playlists.add(playlist);
        playlistAddSongAdapter.notifyDataSetChanged();
    }
}
