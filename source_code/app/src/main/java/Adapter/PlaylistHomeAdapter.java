package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.Playlist;

public class PlaylistHomeAdapter extends RecyclerView.Adapter<PlaylistHomeAdapter.ItemRecentMusicHolder>  {
    Context context;
    HandleListeningItemClicked<Playlist> handleListeningItemClicked;
    HandleListeningItemLongClicked<Playlist> handleListeningItemLongClicked;
    ArrayList<Playlist> arrayList ;

    public PlaylistHomeAdapter(Context context, ArrayList<Playlist> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item_home, parent, false);
        return new PlaylistHomeAdapter.ItemRecentMusicHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Playlist playlist = arrayList.get(position);
        Glide.with(context)
                .load(playlist.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnails);

        holder.tvNamePlaylist.setText(playlist.getTitle());
        holder.tvNumberSong.setText(playlist.getCountSong() + " songs");

        holder.cardMainPlaylist.setOnLongClickListener(v -> {

            if( handleListeningItemLongClicked != null ) {
                handleListeningItemLongClicked.onLongClick(playlist);
                return true;
            }
            return false;

        });

        holder.cardMainPlaylist.setOnClickListener(v -> {
            if( handleListeningItemClicked != null ) {
                handleListeningItemClicked.onClick(playlist);
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

    public void setOnItemLongClickListener(HandleListeningItemLongClicked<Playlist> onItemLongClickListener) {
        this.handleListeningItemLongClicked =  onItemLongClickListener;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ImageView thumbnails ;
        MaterialCardView cardMainPlaylist;
        TextView  tvNamePlaylist, tvNumberSong, tvDesPlaylist;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            thumbnails = itemView.findViewById(R.id.thumbnails_playlist);
            tvNamePlaylist = itemView.findViewById(R.id.tv_name_playlist);
            tvNumberSong = itemView.findViewById(R.id.tv_number_song);
            tvDesPlaylist = itemView.findViewById(R.id.tv_des_playlist);
            cardMainPlaylist = itemView.findViewById(R.id.card_main_playlist);
        }
    }

}
