package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.Song;
import utils.Util;

public class ItemPlayingAdapter extends RecyclerView.Adapter<ItemPlayingAdapter.ItemArtistHolder>  {
    private Context context;
    private HandleListeningItemClicked<Song> handleListeningItemClicked;
    private HandleListeningItemClicked<Song> handleListeningItemRemoveClicked;
    private HandleListeningItemLongClicked<Song> handleListeningItemLongClicked;
    private ArrayList<Song> songs ;

    public ItemPlayingAdapter(Context context, ArrayList<Song> data) {
        this.songs = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playing_item, parent, false);
        return new ItemPlayingAdapter.ItemArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemArtistHolder holder, int position) {
        Song song = songs.get(position);

        holder.tvSongName.setText(song.getTitle());
        holder.tvNameArtist.setText(song.getUserUpload().getUsername());
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnails);

        holder.itemView.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( handleListeningItemClicked != null)
                handleListeningItemClicked.onClick(song);
        });

        holder.btnRemoveSongPlaying.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( handleListeningItemRemoveClicked != null)
                handleListeningItemRemoveClicked.onClick(song);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public void setOnItemClickListener(HandleListeningItemClicked<Song> onItemClickListener) {
        this.handleListeningItemClicked =  onItemClickListener;
    }

    public void setOnItemRemoveClickListener(HandleListeningItemClicked<Song> onItemRemoveClickListener) {
        this.handleListeningItemRemoveClicked =  onItemRemoveClickListener;
    }

    public void setOnItemLongClickListener(HandleListeningItemLongClicked<Song> onItemLongClickListener) {
        this.handleListeningItemLongClicked = onItemLongClickListener;
    }

    public static class ItemArtistHolder extends RecyclerView.ViewHolder{

        ShapeableImageView btnRemoveSongPlaying, btnPlaySongPlaying, thumbnails;
        TextView tvSongName, tvNameArtist;
        public ItemArtistHolder(@NonNull View itemView) {
            super(itemView);

            tvSongName = itemView.findViewById(R.id.tv_name_song);
            tvNameArtist = itemView.findViewById(R.id.tv_name_artist);
            btnRemoveSongPlaying = itemView.findViewById(R.id.btn_remove_item);
            btnPlaySongPlaying = itemView.findViewById(R.id.btn_play_now_music);
            thumbnails = itemView.findViewById(R.id.song_thumbnails);
        }
    }

}
