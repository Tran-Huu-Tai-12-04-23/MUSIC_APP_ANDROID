package BottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import Model.Playlist;

public class BottomSheetActionPlaylist extends BottomSheetDialogFragment {
    private BottomSheetBehavior<View> bBehavior;
    private Playlist playlist;

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_playlist, null);
        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());

        ImageView thumbnails = view.findViewById(R.id.thumbnails);
        TextView tvNamePlaylist, tvNumberSong;
        tvNamePlaylist = view.findViewById(R.id.tv_name_playlist);
        tvNumberSong = view.findViewById(R.id.tv_number_song);

        if(playlist != null ) {
            Glide.with(requireContext())
                    .load(playlist.getThumbnails())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(thumbnails);
            tvNamePlaylist.setText(playlist.getTitle());
            tvNumberSong.setText(playlist.getCountSong() + " songs");
        }

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
