package BottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.BottomSheetArtistBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import DTO.ResponseData;
import Interface.BottomSheetAddArtistInteractionListener;
import Model.Follow;
import Model.Playlist;
import Model.User;
import Service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Util;

public class BottomSheetActionArtist extends BottomSheetDialogFragment {
    private BottomSheetAddArtistInteractionListener bottomSheetAddArtistInteractionListener;
    private BottomSheetArtistBinding binding;
    private BottomSheetBehavior<View> bBehavior;
    private User user;
    private User userTarget;
    private Boolean isFollowed;


    @Override
    public void onAttach(@NonNull Context context) {
        if (getParentFragment() instanceof BottomSheetAddArtistInteractionListener) {
            bottomSheetAddArtistInteractionListener = (BottomSheetAddArtistInteractionListener) getParentFragment();
        }
        super.onAttach(context);
    }
    public void setUserTarget(User userTarget) {
        this.userTarget = userTarget;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = BottomSheetArtistBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(binding.getRoot());
        bBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());

        if( userTarget != null ) {
            Glide.with(requireContext())
                    .load(userTarget.getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                    .into(binding.thumbnails);
            binding.tvNameArtist.setText(userTarget.getUsername());
            if( userTarget.getTotalFollow() != null && userTarget.getTotalFollow() > 0 ) {
                binding.tvTotalFollower.setText(Util.convertToShortForm(userTarget.getTotalFollow()) + " followers");
            }else {
                binding.tvTotalFollower.setText("No follower");
            }
        }
        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.btnFollow.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if( isFollowed == null ) {
                handleFollow();
                return;
            };
            if (isFollowed) {
                handleUnFollow();
            } else {
                handleFollow();
            }
        });

        initViewDataFollow();
        return dialog;
    }

    private void handleUnFollow() {
        if( user == null || userTarget == null ) {
            return;
        }
        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowee(userTarget);

        ApiService.ApiService.unFollow(follow).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    if(bottomSheetAddArtistInteractionListener != null ) {
                        bottomSheetAddArtistInteractionListener.onChangeUnFollow(userTarget);
                    }
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());

            }
        });
    }

    private void handleFollow() {
        if( user == null || userTarget == null ) {
            return;
        }
        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowee(userTarget);

        ApiService.ApiService.follow(follow).enqueue(new Callback<ResponseData<Playlist>>() {
            @Override
            public void onResponse(Call<ResponseData<Playlist>> call, Response<ResponseData<Playlist>> response) {
                if( response.isSuccessful()) {
                    if(bottomSheetAddArtistInteractionListener != null ) {
                        bottomSheetAddArtistInteractionListener.onChangeFollow(userTarget);
                    }
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Playlist>> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());
            }
        });
    }

    private void initViewDataFollow() {
        if(user == null  || userTarget == null ) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowee(userTarget);
        ApiService.ApiService.isExistFollow(follow).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( response.isSuccessful()) {
                    isFollowed = response.body();
                    handleUpdateUI();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());
                isFollowed = false;
            }
        });
    }

    private void handleUpdateUI() {
        if( isFollowed ) {
            binding.btnFollow.setText("Hủy theo dõi");
            binding.btnFollow.setIconResource(R.drawable.close_md_svgrepo_com);
        }else {
            binding.btnFollow.setText("Theo dõi");
            binding.btnFollow.setIconResource(R.drawable.baseline_person_add_alt_1_24);
        }
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
