package BottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Adapter.ItemPlayingAdapter;
import Constanst.Constant;
import Firebase.FirebaseService;
import Interface.BottomSheetPlayMusicInteractionListener;
import Interface.HandleListeningItemClicked;
import Model.CurrentPlaylist;
import Model.Song;
import Service.PlayMusicService;
import utils.LoadingDialog;
import utils.Util;

public class BottomSheetPlayingList extends BottomSheetDialogFragment {
    private BottomSheetPlayMusicInteractionListener mBottomSheetPlayMusicListener;
    private BottomSheetBehavior<View> bBehavior;
    private CurrentPlaylist currentPlaylist;
    private RecyclerView containerPlayingList;
    private CurrentPlaylist prevCurrentPlaylist;
    private ArrayList<Song> songs;
    private Song currentSong;
    TextView tvNameSong, tvNameArtist, tvDuration, tvNumberSong, tvTotalDurationSong;
    ShapeableImageView notifiPlaying, thumbnails;

    private LoadingDialog loadingDialog;
    private FirebaseService firebaseService;

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public CurrentPlaylist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(CurrentPlaylist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        mBottomSheetPlayMusicListener = (BottomSheetPlayMusicInteractionListener) getParentFragment();
        super.onAttach(context);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(requireActivity());
        firebaseService = new FirebaseService(requireActivity().getApplicationContext());

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_playing_list, null);

        thumbnails = view.findViewById(R.id.song_thumbnails);
        tvNameArtist = view.findViewById(R.id.tv_name_artist);
        tvNameSong = view.findViewById(R.id.tv_name_song);
        tvDuration = view.findViewById(R.id.tv_duration_song);
        tvNumberSong = view.findViewById(R.id.tv_number_song);
        tvTotalDurationSong = view.findViewById(R.id.tv_total_duration_song);
        containerPlayingList = view.findViewById(R.id.container_list_song_playing);
        notifiPlaying = view.findViewById(R.id.notifi_playing);

        YoYo.with(Techniques.Tada)
                .duration(2000)
                .repeat(500)
                .playOn(notifiPlaying);
        

        if( currentSong == null ) return dialog;
        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());

        initViewCurrentSong();

        initData();
        return dialog;
    }

    @SuppressLint("SetTextI18n")
    private void initViewPlayingList() {
        if (!isAdded() || getContext() == null) return;
        if( this.currentPlaylist.getSongs() == null ) return ;
        this.songs = this.currentPlaylist.getSongs()
                .stream()
                .filter(song -> song.getId() != currentSong.getId())
                .collect(Collectors.toCollection(ArrayList::new));
        double totalDuration = songs.stream()
                .mapToDouble(Song::getDuration)
                .reduce(0, Double::sum);
        tvTotalDurationSong.setText(Util.convertDurationToString((long) totalDuration) + "s");
        tvNumberSong.setText(songs.size() + " songs");
        Glide.with(requireContext())
                .load(currentSong.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnails);

        LinearLayoutManager layoutManagerNewMusic = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ItemPlayingAdapter itemPlayingAdapter = new ItemPlayingAdapter(getContext(),songs);

        itemPlayingAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                firebaseService.changeCurrentSong(data, new FirebaseService.OnSaveCompleteListener() {
                    @Override
                    public void onSaveComplete(boolean isSuccess) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.CURRENT_SONG,data);
                        Intent intent = new Intent(getContext(), PlayMusicService.class);
                        intent.putExtras(bundle);
                        // Start the service
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            requireContext().startForegroundService(intent);
                        } else {
                            requireContext().startService(intent);
                        }
                    }
                });
            }
        });

        itemPlayingAdapter.setOnItemRemoveClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                removeSong(data);
            }
        });

        containerPlayingList.setLayoutManager(layoutManagerNewMusic);
        containerPlayingList.setAdapter(itemPlayingAdapter);
    }

    private void initViewCurrentSong() {
        if (!isAdded() || getContext() == null) return;
        tvNameSong.setText(this.currentSong.getTitle());
        tvDuration.setText(Util.convertDurationToString((long) this.currentSong.getDuration()));

        if( this.currentSong.getUserUpload() != null)
            tvNameArtist.setText(this.currentSong.getUserUpload().getUsername());
    }
    private void removeSong(Song data) {
        prevCurrentPlaylist = currentPlaylist;
        List<Song> songs = currentPlaylist.getSongs();
        songs = songs.stream()
                .filter(s -> s.getId() != data.getId())
                .collect(Collectors.toList());
        firebaseService.replaceSongsInPlaylist(songs, new FirebaseService.OnSaveCompleteListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onSaveComplete(boolean isSuccess) {
                if( isSuccess ) {
                    Snackbar.make(getDialog().getWindow().getDecorView() , "Xóa bài hát khỏi danh sách phát", Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebaseService.replaceSongsInPlaylist(prevCurrentPlaylist.getSongs(), new FirebaseService.OnSaveCompleteListener() {
                                @Override
                                public void onSaveComplete(boolean isSuccess) {

                                }
                            });
                        }
                    }).show();

                }
            }
        });
    }

    private void initData() {
        loadingDialog.startLoading();
        firebaseService.addPlaylistChangeListener(new FirebaseService.OnPlaylistChangeListener() {
            @Override
            public void onPlaylistChanged(CurrentPlaylist playinglist) {
                if (playinglist != null) {
                    currentPlaylist = playinglist;
                    currentSong = playinglist.getCurrentSong();
                    if(mBottomSheetPlayMusicListener != null ) {
                        mBottomSheetPlayMusicListener.onCurrentListChange(currentPlaylist);
                        mBottomSheetPlayMusicListener.onCurrentSongChange(currentSong);
                    }
                    initViewCurrentSong();
                    initViewPlayingList();
                };
                loadingDialog.stopLoading();
            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
