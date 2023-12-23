package Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.musicplayer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import Model.Song;

public class SongLikedAdapter extends RecyclerView.Adapter<SongLikedAdapter.ItemRecentMusicHolder>  {
    Context context;
    OnListeningItemClicked onListeningItemClicked;
    ArrayList<Song> arrayList ;

    public SongLikedAdapter(Context context, ArrayList<Song> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_liked_item, parent, false);
        MaterialButton btnActionLike = view.findViewById(R.id.btn_action_like);

        btnActionLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand)
                        .duration(500)
                        .playOn(btnActionLike);
            }
        });

        return new SongLikedAdapter.ItemRecentMusicHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Song song = arrayList.get(position);
        Glide.with(context)
                .load(song.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.thumbnails);

        holder.tvNameArtist.setText(song.getUserUpload().getUsername());
        holder.tvNameSong.setText(song.getTitle().substring(0,1).toUpperCase() + song.getTitle().substring(1).toLowerCase());

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

    public void setOnItemClickListener(OnListeningItemClicked onItemClickListener) {
        this.onListeningItemClicked =  onItemClickListener;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ImageView thumbnails ;
        MaterialButton btnActionLike;
        TextView tvNameSong, tvNameArtist;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            thumbnails = itemView.findViewById(R.id.thumbnails_playlist);
            btnActionLike = itemView.findViewById(R.id.btn_action_like);
            tvNameSong = itemView.findViewById(R.id.tv_name_song);
            tvNameArtist = itemView.findViewById(R.id.tv_name_artist);
        }
    }

    public interface OnListeningItemClicked {
        void onClick(ImageView imageView, MaterialButton btnActionLike, String url);
    }

}
