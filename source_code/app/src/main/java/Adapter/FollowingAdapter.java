package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.musicplayer.databinding.ArtistItemBinding;

import java.util.ArrayList;

import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Model.User;
import utils.Util;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ItemRecentMusicHolder>  {
    private HandleListeningItemClicked<User> handleListeningItemClicked;
    private HandleListeningItemLongClicked<User> handleListeningItemLongClicked;
    private final Context context;
    private final ArrayList<User> arrayList ;

    public FollowingAdapter(Context context, ArrayList<User> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtistItemBinding binding = ArtistItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemRecentMusicHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        User user = arrayList.get(position);
        Glide.with(context)
                .load(user.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.avatar);

        holder.binding.tvNameArtist.setText(user.getUsername());

        holder.binding.avatar.setOnClickListener(v -> {
            YoYo.with(Techniques.Bounce)
                    .duration(500)
                    .playOn(v);
            // handleListeningItemClicked.onClick(holder.imgView, arrayList.get(holder.getAdapterPosition()));
        });

        holder.binding.avatar.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( handleListeningItemClicked != null ) {
                handleListeningItemClicked.onClick(user);
            }
        });
        holder.binding.avatar.setOnLongClickListener(v -> {
            Util.applyClickAnimation(v);
            if( handleListeningItemLongClicked != null ) {
                handleListeningItemLongClicked.onLongClick(user);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnClickListener(HandleListeningItemClicked<User> handleListeningItemClicked) {
        this.handleListeningItemClicked = handleListeningItemClicked;
    }

    public void setOnLongClickListener(HandleListeningItemLongClicked<User> handleListeningItemLongClicked) {
        this.handleListeningItemLongClicked = handleListeningItemLongClicked;
    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder {
        ArtistItemBinding binding;

        public ItemRecentMusicHolder(ArtistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
