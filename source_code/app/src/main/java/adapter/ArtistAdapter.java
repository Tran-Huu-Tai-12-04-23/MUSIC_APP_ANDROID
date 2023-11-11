package adapter;

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

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemArtistHolder>  {
    Context context;
    HandleListeningItemClicked handleListeningItemClicked;
    ArrayList<String> arrayList ;

    public ArtistAdapter(Context context, ArrayList<String> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_item, parent, false);
        return new ArtistAdapter.ItemArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemArtistHolder holder, int position) {
        Glide.with(context)
                .load(arrayList.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.avatar);

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
    public static class ItemArtistHolder extends RecyclerView.ViewHolder{
        ImageView  avatar ;
        public ItemArtistHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
        }
    }

}
