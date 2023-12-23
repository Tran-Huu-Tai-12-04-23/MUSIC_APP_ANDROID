package BottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.BottomSheetAddPlaylistBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.Objects;

import DTO.ResponseData;
import Interface.BottomSheetAddPlaylistInteractionListener;
import Model.Playlist;
import Model.User;
import Service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

public class BottomSheetAddPlaylist extends BottomSheetDialogFragment {
    private BottomSheetAddPlaylistInteractionListener bottomSheetAddPlaylistInteractionListener;
    private BottomSheetBehavior<View> bBehavior;
    private User user;
    LoadingDialog loadingDialog;

    public void setUser(User user) {
        this.user = user;
    }
    private BottomSheetAddPlaylistBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        bottomSheetAddPlaylistInteractionListener = (BottomSheetAddPlaylistInteractionListener) getParentFragment();
        super.onAttach(context);
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottom_sheet_add_playlist, null);

        dialog.setContentView(view);
        bBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddPlaylistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        loadingDialog = new LoadingDialog(requireActivity());

        initUser();

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Bounce)
                        .duration(500)
                        .playOn(v);
                dismiss();
            }
        });

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Bounce)
                        .duration(500)
                        .playOn(v);
                String title = Objects.requireNonNull(binding.inputNamePlaylist.getEditText()).getText().toString();

                if( title.equals("")) return;

                Playlist playlist = new Playlist();
                playlist.setCreateAt(new Date());
                playlist.setTitle(title);
                boolean isPrivate = binding.switchPrivate.isChecked();
                playlist.setPrivate(isPrivate);

                addPlaylist(playlist);

            }
        });

        YoYo.with(Techniques.BounceInUp)
                .duration(500)
                .playOn(view);

        return view;
    }

    private void initUser() {
        if (requireActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();
        }
    }

    private void addPlaylist(Playlist playlist) {
        if( user == null ) return;
        playlist.setUser(user);
        loadingDialog.startLoading();

        ApiService.ApiService.addPlaylistUser(playlist).enqueue(new Callback<ResponseData<Playlist>>() {
            @Override
            public void onResponse(Call<ResponseData<Playlist>> call, Response<ResponseData<Playlist>> response) {
                loadingDialog.stopLoading();
                if( response.isSuccessful() ) {
                    ResponseData<Playlist> playlistResponseData = response.body();
                    assert playlistResponseData != null;
                    if( playlistResponseData.getData() != null ) {
                        if( bottomSheetAddPlaylistInteractionListener != null ) {
                            bottomSheetAddPlaylistInteractionListener.onChangeAddPlaylist(playlistResponseData.getData());
                        }
                        dismiss();
                    }else {
                        Snackbar.make(requireView(), playlistResponseData.getMessage(), Snackbar.LENGTH_LONG)
                                .show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Playlist>> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("Add playlist by user ", t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
