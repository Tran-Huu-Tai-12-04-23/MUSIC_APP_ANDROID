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
import Model.User;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemArtistHolder>  {
    private Context context;
    private HandleListeningItemClicked<User> handleListeningItemClicked;
    private HandleListeningItemLongClicked<User> handleListeningItemLongClicked;
    private ArrayList<User> arrayList ;

    public ArtistAdapter(Context context, ArrayList<User> data) {
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
        User user = arrayList.get(position);
        Glide.with(context)
                .load(user.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.avatar);

        holder.tvNameArtist.setText(user.getUsername());

        holder.cardMainArtist.setOnClickListener(v -> {
            if( handleListeningItemClicked != null ) {
                handleListeningItemClicked.onClick(user);
            }
        });

        holder.cardMainArtist.setOnLongClickListener(v -> {
            if( handleListeningItemLongClicked != null ) {
                handleListeningItemLongClicked.onLongClick(user);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public void setOnItemClickListener(HandleListeningItemClicked<User> onItemClickListener) {
        this.handleListeningItemClicked =  onItemClickListener;
    }

    public void setOnItemLongClickListener(HandleListeningItemLongClicked<User> onItemLongClickListener) {
        this.handleListeningItemLongClicked = onItemLongClickListener;
    }

    public static class ItemArtistHolder extends RecyclerView.ViewHolder{
        ImageView  avatar ;
        TextView tvNameArtist;
        MaterialCardView cardMainArtist;
        public ItemArtistHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
            tvNameArtist = itemView.findViewById(R.id.tv_name_artist);
            cardMainArtist = itemView.findViewById(R.id.card_main_artist);
        }
    }

}
