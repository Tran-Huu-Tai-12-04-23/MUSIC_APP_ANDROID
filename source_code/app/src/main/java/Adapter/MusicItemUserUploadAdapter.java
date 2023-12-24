package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.databinding.MusicItemUserUploadBinding;

import java.util.ArrayList;

import Interface.HandleCheckedItem;
import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.Song;

public class MusicItemUserUploadAdapter extends RecyclerView.Adapter<MusicItemUserUploadAdapter.ItemRecentMusicHolder> {
    private final Context context;
    private HandleListeningItemClicked<Song> handleListeningItemClicked;
    private HandleListeningItemLongClicked<Song> handleListeningItemLongClicked;
    private HandleCheckedItem<Song> handleCheckedItem;
    private final ArrayList<Song> arrayList;

    public MusicItemUserUploadAdapter(Context context, ArrayList<Song> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MusicItemUserUploadBinding binding = MusicItemUserUploadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemRecentMusicHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Song song = arrayList.get(position);
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.binding.thumbnails);

        holder.binding.nameSong.setText(song.getTitle());
        holder.binding.nameArtist.setText(song.getUserUpload().getUsername());
        holder.binding.modeSwitch.setChecked(song.getPrivate());

        holder.binding.cardMain.setOnClickListener(v -> {
            if (handleListeningItemClicked != null) {
                handleListeningItemClicked.onClick(song);
            }
        });

        holder.binding.modeSwitch.setOnClickListener(v -> {
            if( handleCheckedItem != null ) {
                handleCheckedItem.onChecked(song);
            }
        });

        holder.binding.cardMain.setOnLongClickListener(v -> {
            if (handleListeningItemLongClicked != null) {
                handleListeningItemLongClicked.onLongClick(song);
            }
            return false;
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

    public void setOnCheckedItem(HandleCheckedItem<Song> onCheckedItem) {
        this.handleCheckedItem = onCheckedItem;
    }
    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder {
        private final MusicItemUserUploadBinding binding;

        public ItemRecentMusicHolder(MusicItemUserUploadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
