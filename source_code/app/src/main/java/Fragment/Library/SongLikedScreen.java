package Fragment.Library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.FragmentSongLikedScreenBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import Adapter.SongLikedAdapter;
import DTO.ResponseData;
import Model.Song;
import Model.User;
import Service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;


public class SongLikedScreen extends Fragment {
    private FragmentSongLikedScreenBinding binding;
    private ArrayList<Song> songs;
    private LoadingDialog loadingDialog;
    private SongLikedAdapter songLikedAdapter;
    private User user;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SongLikedScreen() {
        // Required empty public constructor
    }


    public static SongLikedScreen newInstance(String param1, String param2) {
        SongLikedScreen fragment = new SongLikedScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongLikedScreenBinding.inflate(inflater, container, false);
        this.songs = new ArrayList<>();
        loadingDialog = new LoadingDialog(getActivity());
        initUser();
        initData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        this.songLikedAdapter = new SongLikedAdapter(getContext(),  songs);

        this.songLikedAdapter.setOnItemClickListener(new SongLikedAdapter.OnListeningItemClicked() {
            @Override
            public void onClick(ImageView imageView, MaterialButton btnActionLike, String url) {
                Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        binding.containerSongLiked.setLayoutManager(layoutManager);
        binding.containerSongLiked.setAdapter(songLikedAdapter);

        binding.btnBack.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            homeActivity.onBackPressed();
        });

        return  binding.getRoot();
    }

    private void initUser() {
        if(requireActivity() instanceof  HomeActivity ) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();
            if( user == null) return;

            Glide.with(requireContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.baseline_person_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                    .into(binding.avatar);
        }
    }


    private void initData() {
        if( user == null ) return;
        loadingDialog.startLoading();
        ApiService.ApiService.getSongLiked(user.getId()).enqueue(new Callback<ResponseData<List<Song>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Song>>> call, Response<ResponseData<List<Song>>> response) {
                loadingDialog.stopLoading();
                if( !response.isSuccessful()) return;

                ResponseData<List<Song>> data = response.body();

                assert data != null;
                List<Song> songsApi = data.getData();

                songs.addAll(songsApi);
                songLikedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseData<List<Song>>> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.stopLoading();
            }
        });


    }
    @Override
    public void onDestroy() {
        loadingDialog.destroyDialog();
        super.onDestroy();
    }
}