package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemRecentMusicHolder>  {
    Context context;
    HandleListeningItemClicked handleListeningItemClicked;
    ArrayList<String> arrayList ;

    public PlaylistAdapter(Context context, ArrayList<String> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter.ItemRecentMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Glide.with(context)
                .load(arrayList.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.thumbnails);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handleListeningItemClicked.onClick(holder.imgView, arrayList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(HandleListeningItemClicked onItemClickListener) {
        this.handleListeningItemClicked =  onItemClickListener;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ImageView thumbnails ;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            thumbnails = itemView.findViewById(R.id.thumbnails_playlist);
        }
    }

}
