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

public class ItemCarouselAdapter extends RecyclerView.Adapter<ItemCarouselAdapter.ItemCarouselHolder> {

    Context context;
    OnItemClickListener onItemClickListener;

    ArrayList<String> arrayList ;

    public ItemCarouselAdapter(Context context, ArrayList<String> data) {
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
        Glide.with(context)
                .load(arrayList.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.imgView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(holder.imgView, arrayList.get(holder.getAdapterPosition()));
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


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(ImageView imageView, String url);
    }
}
