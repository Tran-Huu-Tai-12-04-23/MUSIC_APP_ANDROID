package Adapter;

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
import Model.Song;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemAdapter.ItemRecentMusicHolder>  {
    Context context;
    HandleListeningItemClicked<Song> handleListeningItemClicked;
    HandleListeningItemLongClicked<Song> handleListeningItemLongClicked;
    ArrayList<Song> arrayList ;
    private Boolean isHome;

    public MusicItemAdapter(Context context, ArrayList<Song> data, Boolean isHome) {
        this.arrayList = data;
        this.context = context;
        this.isHome = isHome;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if( isHome ) {
            View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
            return new MusicItemAdapter.ItemRecentMusicHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.music_item_artist_detail, parent, false);
            return new MusicItemAdapter.ItemRecentMusicHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Song song = arrayList.get(position);
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.thumbnails);

        holder.nameSong.setText(song.getTitle());
        holder.nameArtist.setText(song.getUserUpload().getUsername());

        holder.cardMain.setOnClickListener(v -> {
            if (handleListeningItemClicked != null) {
                handleListeningItemClicked.onClick(song);
            }
        });

        holder.cardMain.setOnLongClickListener(v -> {
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

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ImageView  thumbnails ;
        TextView nameArtist, nameSong ;
        MaterialCardView cardMain;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            thumbnails = itemView.findViewById(R.id.thumbnails);
            nameArtist = itemView.findViewById(R.id.name_artist);
            nameSong = itemView.findViewById(R.id.name_song);
            cardMain = itemView.findViewById(R.id.card_main);
        }
    }

}
