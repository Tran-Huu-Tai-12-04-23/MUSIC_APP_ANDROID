package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.databinding.ItemDetailPlaylistBinding;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.DetailPlaylist;

public class ItemDetailPlaylistAdapter extends RecyclerView.Adapter<ItemDetailPlaylistAdapter.ItemDetailPlaylistHolder> {

    private final Context context;
    private HandleListeningItemClicked<DetailPlaylist> handleListeningItemClicked;
    private HandleListeningItemLongClicked<DetailPlaylist> handleListeningItemLongClicked;
    private final ArrayList<DetailPlaylist> arrayList;

    public ItemDetailPlaylistAdapter(Context context, ArrayList<DetailPlaylist> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemDetailPlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailPlaylistBinding binding = ItemDetailPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemDetailPlaylistHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDetailPlaylistHolder holder, int position) {
        DetailPlaylist detailPlaylist = arrayList.get(position);
        Glide.with(context)
                .load(detailPlaylist.getSong().getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.songThumbnails);

        holder.binding.tvNameSong.setText(detailPlaylist.getSong().getTitle());
        holder.binding.tvNameArtist.setText(detailPlaylist.getSong().getUserUpload().getUsername());

        holder.binding.cardMainSearch.setOnClickListener(v -> {
            if (handleListeningItemClicked != null) {
                handleListeningItemClicked.onClick(detailPlaylist);
            }
        });

        holder.binding.cardMainSearch.setOnLongClickListener(v -> {
            if (handleListeningItemLongClicked != null) {
                handleListeningItemLongClicked.onLongClick(detailPlaylist);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(HandleListeningItemClicked<DetailPlaylist> onItemClickListener) {
        this.handleListeningItemClicked = onItemClickListener;
    }

    public void setOnItemLongClickListener(HandleListeningItemLongClicked<DetailPlaylist> onItemLongClickListener) {
        this.handleListeningItemLongClicked = onItemLongClickListener;
    }

    public static class ItemDetailPlaylistHolder extends RecyclerView.ViewHolder {
        private final ItemDetailPlaylistBinding binding;

        public ItemDetailPlaylistHolder(ItemDetailPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
