package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Model.Song;

public class ItemCarouselAdapter extends RecyclerView.Adapter<ItemCarouselAdapter.ItemCarouselHolder> {

    Context context;
    HandleListeningItemClicked<Song> handleListeningItemClicked;

    ArrayList<Song> arrayList ;

    public ItemCarouselAdapter(Context context, ArrayList<Song> data) {
        this.arrayList = data;
        this.context = context;
    }


    @NonNull
    @Override
    public ItemCarouselHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item_carousel, parent, false);
        return new ItemCarouselHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCarouselHolder holder, int position) {
        Song song = arrayList.get(position);
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.imgView);
        holder.imgView.setOnClickListener(v -> {
            if( handleListeningItemClicked != null ) {
                handleListeningItemClicked.onClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ItemCarouselHolder extends RecyclerView.ViewHolder{
        ImageView imgView ;
        public ItemCarouselHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imageView);
        }
    }


    public void setOnItemClickListener(HandleListeningItemClicked<Song>  handleListeningItemClicked) {
        this.handleListeningItemClicked = handleListeningItemClicked;
    }

    public interface OnItemClickListener{
        void onClick(ImageView imageView, String url);
    }
}
