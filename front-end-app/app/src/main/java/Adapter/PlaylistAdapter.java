package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.databinding.ItemPlaylistBinding;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.Playlist;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemRecentMusicHolder>  {
    Context context;
    HandleListeningItemClicked<Playlist> handleListeningItemClicked;
    HandleListeningItemLongClicked<Playlist> handleListeningItemLongClicked;
    ArrayList<Playlist> arrayList ;

    public PlaylistAdapter(Context context, ArrayList<Playlist> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use View Binding to inflate the layout
        ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemRecentMusicHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(HandleListeningItemClicked<Playlist> onItemClickListener) {
        this.handleListeningItemClicked =  onItemClickListener;
    }
    public void setOnItemLongClickListener(HandleListeningItemLongClicked<Playlist> onItemLongClickListener) {
        this.handleListeningItemLongClicked =  onItemLongClickListener;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ItemPlaylistBinding binding;
        public ItemRecentMusicHolder(@NonNull ItemPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
