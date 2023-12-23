package BottomSheet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.activity.Library.AddSongToPlaylistActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import Firebase.FirebaseService;
import Model.Song;
import Model.User;
import io.github.muddz.styleabletoast.StyleableToast;

public class BottomSheetActionSong extends BottomSheetDialogFragment {
    private BottomSheetBehavior<View> bBehavior;
    private Song song;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    private FirebaseService firebaseService;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        firebaseService = new FirebaseService(requireContext());
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_songs_action, null);

        ShapeableImageView thumbnails = view.findViewById(R.id.song_thumbnails);
        TextView tvNameSong, tvNameArtist;
        MaterialButton btnAddSongIntoPlayingList, btnAddSongToPlaylist, btnWatchArtist, btnDownload, btnWatchPlaylistSong, btnShare;


        tvNameArtist = view.findViewById(R.id.tv_name_artist);
        tvNameSong = view.findViewById(R.id.tv_name_song);

        btnAddSongIntoPlayingList = view.findViewById(R.id.btn_add_song_to_playing_list);
        btnAddSongToPlaylist = view.findViewById(R.id.btn_add_song_to_playlist);
        btnWatchArtist = view.findViewById(R.id.btn_watch_artist);
        btnDownload = view.findViewById(R.id.btn_download);
        btnShare = view.findViewById(R.id.btn_share);

        if( song != null ) {
            tvNameArtist.setText(song.getUserUpload().getUsername());
            tvNameSong.setText(song.getTitle());
            Glide.with(requireContext())
                    .load(song.getThumbnails())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(thumbnails);
        }
        //handle action

        btnAddSongIntoPlayingList.setOnClickListener(v -> {
            firebaseService.addSongToPlaylist(song, new FirebaseService.OnSaveCompleteListener() {
                @Override
                public void onSaveComplete(boolean isSuccess) {
                    if(getContext() != null ) {
                        StyleableToast.makeText(requireContext(), "Thêm bài hát vào danh sách phát thành công!", Toast.LENGTH_SHORT, R.style.toast_success).show();
                        dialog.dismiss();
                    }

                }
            });
        });

        btnAddSongToPlaylist.setOnClickListener(v -> {

            HomeActivity homeAc = (HomeActivity)requireActivity();
            User user =homeAc.getUser();
            if( user== null  ) return;
            Intent intent = new Intent(getContext(), AddSongToPlaylistActivity.class);
            intent.putExtra("songId", song.getId());
            intent.putExtra("userId", user.getId());
            startActivity(intent);
        });


        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());

        return dialog;
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
