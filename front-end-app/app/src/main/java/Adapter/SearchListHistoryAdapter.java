package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Interface.HandleListeningOpenMenuClicked;
import Interface.HandleListeningRemove;
import Model.Song;
import utils.Util;

public class SearchListHistoryAdapter extends RecyclerView.Adapter<SearchListHistoryAdapter.ItemRecentMusicHolder>  {
    Context context;
    HandleListeningItemClicked<Song> handleListeningItemClicked;
    HandleListeningItemLongClicked<Song> handleListeningItemLongClicked;
    HandleListeningRemove<Song> handleListeningRemove;
    ArrayList<Song> arrayList ;

    public SearchListHistoryAdapter(Context context, ArrayList<Song> data) {
        this.arrayList = data;
        this.context = context;
    }
    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item_history, parent, false);
        return new SearchListHistoryAdapter.ItemRecentMusicHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Song song = arrayList.get(position);

        if( song == null ) return;
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.thumbnails);

        holder.tvNameSong.setText(song.getTitle());
        holder.tvNameArtist.setText(song.getUserUpload().getUsername());

        holder.cardMainSearch.setOnClickListener(v -> {
            if( handleListeningItemClicked != null  ) {
                handleListeningItemClicked.onClick(song);
            }
        });

        holder.cardMainSearch.setOnLongClickListener(v -> {
            if( handleListeningItemLongClicked !=  null ) {
                handleListeningItemLongClicked.onLongClick(song);
                return true;
            }
            return false;
        });

        holder.btnRemoveSongHistory.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( handleListeningRemove !=  null ) {
                handleListeningRemove.onRemove(song);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(HandleListeningItemClicked<Song> onItemClickListener) {
        this.handleListeningItemClicked =  onItemClickListener;
    }

    public void setOnItemLongClickListener(HandleListeningItemLongClicked<Song> onItemLongClickListener) {
        this.handleListeningItemLongClicked =  onItemLongClickListener;
    }

    public void setOnRemoveItemClick(HandleListeningRemove<Song> onRemove) {
        this.handleListeningRemove =  onRemove;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ImageView thumbnails ;
        TextView tvNameSong, tvNameArtist;
        MaterialCardView cardMainSearch;
        ImageFilterView btnRemoveSongHistory;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            thumbnails = itemView.findViewById(R.id.song_thumbnails);
            tvNameSong = itemView.findViewById(R.id.tv_name_song);
            tvNameArtist = itemView.findViewById(R.id.tv_name_artist);
            cardMainSearch = itemView.findViewById(R.id.card_main_search);
            btnRemoveSongHistory = itemView.findViewById(R.id.btn_remove_item);
        }
    }

}
