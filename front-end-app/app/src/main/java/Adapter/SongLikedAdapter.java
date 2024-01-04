package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.databinding.SongLikedItemBinding;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLikedClicked;
import Interface.HandleListeningItemLongClicked;
import Model.Song;

public class SongLikedAdapter extends RecyclerView.Adapter<SongLikedAdapter.ItemRecentMusicHolder> {
    private final Context context;
    private HandleListeningItemClicked<Song> handleListeningItemClicked;
    private HandleListeningItemLikedClicked<Song> handleListeningItemLikedClicked;
    private HandleListeningItemLongClicked<Song> handleListeningItemLongClicked;
    private final ArrayList<Song> arrayList;

    public SongLikedAdapter(Context context, ArrayList<Song> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use View Binding to inflate the layout
        SongLikedItemBinding binding = SongLikedItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemRecentMusicHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Song song = arrayList.get(position);

        // Use View Binding to access views
        holder.binding.tvNameArtist.setText(song.getUserUpload().getUsername());
        holder.binding.tvNameSong.setText(song.getTitle().substring(0, 1).toUpperCase() + song.getTitle().substring(1).toLowerCase());
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.binding.thumbnailsPlaylist);

        holder.binding.mainCard.setOnClickListener(v -> {
            if (handleListeningItemClicked != null) {
                handleListeningItemClicked.onClick(song);
            }
        });

        holder.binding.mainCard.setOnLongClickListener(v -> {
            if (handleListeningItemLongClicked != null) {
                handleListeningItemLongClicked.onLongClick(song);
            }
            return false;
        });

        holder.binding.btnActionLike.setOnClickListener(v -> {
            if (handleListeningItemLikedClicked != null) {
                handleListeningItemLikedClicked.onLike(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(HandleListeningItemClicked<Song> onItemClickListener) {
        this.handleListeningItemClicked = onItemClickListener;
    }

    public void setOnItemLongClickListener(HandleListeningItemLongClicked<Song> onItemLongClickListener) {
        this.handleListeningItemLongClicked = onItemLongClickListener;
    }

    public void setOnItemLikeClickListener(HandleListeningItemLikedClicked<Song> onItemLikeClickListener) {
        this.handleListeningItemLikedClicked = onItemLikeClickListener;
    }

    // Update the constructor to accept a binding parameter
    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder {
        SongLikedItemBinding binding;

        public ItemRecentMusicHolder(SongLikedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
