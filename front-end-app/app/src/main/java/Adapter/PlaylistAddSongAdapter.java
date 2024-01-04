package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.databinding.ItemPlaylistAddSongBinding;

import java.util.ArrayList;

import Interface.HandleCheckedItem;
import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.Playlist;

public class PlaylistAddSongAdapter extends RecyclerView.Adapter<PlaylistAddSongAdapter.ItemPlaylistAddSongViewHolder>  {
    Context context;
    HandleListeningItemClicked<Playlist> handleListeningItemClicked;
    HandleListeningItemLongClicked<Playlist> handleListeningItemLongClicked;
    HandleCheckedItem<Playlist> handleCheckItem;
    ArrayList<Playlist> arrayList ;

    public PlaylistAddSongAdapter(Context context, ArrayList<Playlist> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemPlaylistAddSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use View Binding to inflate the layout
        ItemPlaylistAddSongBinding binding = ItemPlaylistAddSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemPlaylistAddSongViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemPlaylistAddSongViewHolder holder, int position) {
        Playlist playlist = arrayList.get(position);
        Glide.with(context)
                .load(playlist.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.binding.thumbnailsPlaylist);

        holder.binding.tvNamePlaylist.setText(playlist.getTitle());
        holder.binding.tvNumberSong.setText(playlist.getCountSong() + " Bài hát");

        holder.binding.cardMainPlaylist.setOnClickListener(v -> {
            if( handleListeningItemClicked != null ) {
                handleListeningItemClicked.onClick(playlist);
            }
        });

        holder.binding.cardMainPlaylist.setOnLongClickListener(v -> {
            if( handleListeningItemLongClicked != null ) {
                handleListeningItemLongClicked.onLongClick(playlist);
                return true;
            }
            return false;
        });

        holder.binding.btnCheckAdd.setChecked(playlist.getSelected());

        holder.binding.btnCheckAdd.setOnClickListener(v -> {
            if( handleCheckItem != null ) {
                handleCheckItem.onChecked(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(HandleListeningItemClicked<Playlist> onItemClickListener) {
        this.handleListeningItemClicked =  onItemClickListener;
    }

    public void setOnCheckedItem(HandleCheckedItem<Playlist> onCheckedItem) {
        this.handleCheckItem =  onCheckedItem;
    }
    public void setOnItemLongClickListener(HandleListeningItemLongClicked<Playlist> onItemLongClickListener) {
        this.handleListeningItemLongClicked =  onItemLongClickListener;
    }

    public static class ItemPlaylistAddSongViewHolder extends RecyclerView.ViewHolder{
        ItemPlaylistAddSongBinding binding;
        public ItemPlaylistAddSongViewHolder(@NonNull ItemPlaylistAddSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
