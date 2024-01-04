package BottomSheet;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Adapter.CommentListAdapter;
import Model.Comment;
import Model.Song;
import Model.User;
import Service.ApiService;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

public class BottomSheetComment extends BottomSheetDialogFragment {
    private BottomSheetBehavior<View> bBehavior;
    private Song currentSong;
    private CommentListAdapter commentListAdapter;
    private TextInputLayout inputComment;
    private MaterialButton btnSend;
    private long userId;
    private LoadingDialog loadingDialog;

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    private ArrayList<Comment> comments;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(requireActivity());
        SharedPreferences preferences = requireContext().getSharedPreferences("LoginData", MODE_PRIVATE);
        String strUserId = preferences.getString("userId", "");
        if( !strUserId.equals("")) {
            userId = Long.parseLong(strUserId);
        }
        comments = new ArrayList<>();
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_comment, null);
        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());
        bBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());

        MaterialButton btnCloseComment;
        RecyclerView commentListDataRecycleView ;

        btnCloseComment = view.findViewById(R.id.btn_close_comment);
        inputComment = view.findViewById(R.id.input_comment);
        btnSend = view.findViewById(R.id.btn_send_comment);
        btnCloseComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSend.setOnClickListener(v -> {
            String commentContent = inputComment.getEditText().getText().toString();

            if( !commentContent.equals("")) {
                sendComment(commentContent);
            }

        });

//      render data recycle view
        commentListDataRecycleView  = view.findViewById(R.id.container_comment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        commentListAdapter = new CommentListAdapter(requireContext(), comments);

//        commentListAdapter.setOnItemClickListener(new HandleListeningItemClicked() {
//            @Override
//            public void onClick(ImageView imageView, String url) {
//            public void onClick(ImageView imageView, String url) {
//
//            }
//        });

        commentListDataRecycleView.setLayoutManager(layoutManager);
        commentListDataRecycleView.setAdapter(commentListAdapter);

        initComment();


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(commentListDataRecycleView);

        return dialog;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Take action for the swiped item
        }
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int postitionCom = viewHolder.getBindingAdapterPosition();
            Comment comment = comments.get(postitionCom);
            if( comment == null) return;
            if( comment.getUser().getId() != userId ) {
                super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
                return;
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isCurrentlyActive) {
                if( Math.abs(dX) >= 400 ) {
                    int pos = viewHolder.getBindingAdapterPosition();
                    removeComment(pos);
                    super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
                    return;
                }
            }

            if (dX < 0 && dX >= -400 ) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_error))
                        .addActionIcon(R.drawable.trash_bin_2_svgrepo_com)
                        .create()
                        .decorate();
            } else if( dX < -400 ) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_success))
                        .addActionIcon(R.drawable.checked_svgrepo_com)
                        .create()
                        .decorate();

            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    };

    @SuppressLint("NotifyDataSetChanged")
    private void removeComment(int pos) {
        if (pos >= 0 && pos < comments.size()) {
            Comment comment = comments.get(pos);
            if( comment == null ) return;
            ApiService.ApiService.removeCommentById(comment.getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if( response.isSuccessful()) {
                        if (!comments.isEmpty()) {
                            comments.removeIf(c -> c.getId() == comment.getId());
                            commentListAdapter.notifyDataSetChanged();
                        }
                        Snackbar.make(getDialog().getWindow().getDecorView() , "Xóa bình luận", Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoComment(comment);
                            }
                        }).show();

                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.i("TAG", t.getLocalizedMessage());
                }
            });

        }
    }

    @SuppressLint("StaticFieldLeak")
    private void undoComment(Comment comment) {
        ApiService.ApiService.addComment(comment).enqueue(new Callback<Comment>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if( response.isSuccessful() ) {
                    Objects.requireNonNull(inputComment.getEditText()).setText("");
                    initComment();
                }

            }
            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());

            }
        });

    }
    @SuppressLint("StaticFieldLeak")
    private void sendComment(String commentContent) {
        if( currentSong == null ) return;
        Comment comment = new Comment();
        comment.setContent(commentContent);
        User user = new User();
        user.setId(userId);
        comment.setUser(user);
        comment.setSong(currentSong);
        ApiService.ApiService.addComment(comment).enqueue(new Callback<Comment>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if( response.isSuccessful() ) {
                    Objects.requireNonNull(inputComment.getEditText()).setText("");
                    initComment();
                }

            }
            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());

            }
        });

    }


    @SuppressLint("StaticFieldLeak")
    private void initComment() {
        if( currentSong == null ) return;
        ApiService.ApiService.getAllCommentBySong(currentSong.getId()).enqueue(new Callback<List<Comment>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if( response.isSuccessful()) {
                    assert response.body() != null;
                    comments.clear();
                    comments.addAll((ArrayList<Comment>)response.body());
                    commentListAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.i("TAG", Objects.requireNonNull(t.getLocalizedMessage()));
            }
        });


    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
