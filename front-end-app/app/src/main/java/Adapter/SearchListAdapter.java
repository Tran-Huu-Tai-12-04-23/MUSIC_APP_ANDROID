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
import Model.Song;
import utils.Util;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ItemRecentMusicHolder>  {
    Context context;
    HandleListeningItemClicked<Song> handleListeningItemClicked;
    HandleListeningItemLongClicked<Song> handleListeningItemLongClicked;
    HandleListeningOpenMenuClicked<Song> handleListeningOpenMenuClicked;
    ArrayList<Song> arrayList ;

    public SearchListAdapter(Context context, ArrayList<Song> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new SearchListAdapter.ItemRecentMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Song song = arrayList.get(position);
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

        holder.btnOpenMenu.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( handleListeningOpenMenuClicked !=  null ) {
                handleListeningOpenMenuClicked.onOpenMenu(song);
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

    public void setOnOpenMenuListener(HandleListeningOpenMenuClicked<Song> onOpenMenuListener) {
        this.handleListeningOpenMenuClicked =  onOpenMenuListener;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ImageView thumbnails ;
        TextView tvNameSong, tvNameArtist;
        MaterialCardView cardMainSearch;
        ImageFilterView btnOpenMenu;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            thumbnails = itemView.findViewById(R.id.song_thumbnails);
            tvNameSong = itemView.findViewById(R.id.tv_name_song);
            tvNameArtist = itemView.findViewById(R.id.tv_name_artist);
            cardMainSearch = itemView.findViewById(R.id.card_main_search);
            btnOpenMenu = itemView.findViewById(R.id.btn_open_menu);
        }
    }

}
