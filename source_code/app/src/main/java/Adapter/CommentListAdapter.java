package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import Model.Comment;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ItemRecentMusicHolder>  {
    Context context;
//    HandleListeningItemClicked handleListeningItemClicked;
    ArrayList<Comment> arrayList ;

    public CommentListAdapter(Context context, ArrayList<Comment> data) {
        this.arrayList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRecentMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentListAdapter.ItemRecentMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecentMusicHolder holder, int position) {
        Comment comment = arrayList.get(position);

        Glide.with(context)
                .load(comment.getUser().getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.avatar);

        holder.tvContentComment.setText(comment.getContent());
        holder.tvNameUserComment.setText(comment.getUser().getUsername());
        holder.tvCommentDate.setText(comment.getCommentDate().getDay() + "/" + comment.getCommentDate().getMonth() + "/" + (comment.getCommentDate().getYear() + 1900));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

//    public void setOnItemClickListener(HandleListeningItemClicked onItemClickListener) {
//        this.handleListeningItemClicked =  onItemClickListener;
//    }

    public static class ItemRecentMusicHolder extends RecyclerView.ViewHolder{
        ShapeableImageView avatar;
        TextView tvNameUserComment, tvCommentDate, tvContentComment;
        public ItemRecentMusicHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar_account);
            tvNameUserComment = itemView.findViewById(R.id.tv_name_user_comment);
            tvCommentDate = itemView.findViewById(R.id.tv_comment_date);
            tvContentComment = itemView.findViewById(R.id.tv_content_comment);

        }
    }

}
