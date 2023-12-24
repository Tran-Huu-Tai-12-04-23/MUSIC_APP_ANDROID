package Fragment.Library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.FragmentLibraryScreenBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.SearchListAdapter;
import BottomSheet.BottomSheetActionSong;
import DTO.ResponseData;
import Interface.HandleListeningItemClicked;
import Interface.HandleListeningOpenMenuClicked;
import Model.Song;
import Model.User;
import Service.ApiService;
import Service.PlayMusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;


public class LibraryScreen extends Fragment {

    private FragmentLibraryScreenBinding binding;
    private User user;
    private SearchListAdapter searchListAdapter;
    private ArrayList<Song> songs;
    private LoadingDialog loadingDialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public LibraryScreen() {
    }

    public static LibraryScreen newInstance(String param1, String param2) {
        LibraryScreen fragment = new LibraryScreen();
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
        // Inflate the layout for this fragment
        binding = FragmentLibraryScreenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        loadingDialog = new LoadingDialog(requireActivity());
        songs = new ArrayList<>();
        initUser();

        binding.btnNavigatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) requireActivity();
                homeActivity.openPlaylistFragment();
            }
        });

        binding.btnNavigatePlaylistLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) requireActivity();
                homeActivity.openSongLikedFragment();
            }
        });

        binding.btnNavigateFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) requireActivity();
                homeActivity.openFollowing();
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchListAdapter = new SearchListAdapter(getContext(),  songs);

        searchListAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                PlayMusicService.playMusic(requireContext(), data);
            }
        });

        searchListAdapter.setOnOpenMenuListener(new HandleListeningOpenMenuClicked<Song>() {
            @Override
            public void onOpenMenu(Song data) {
                BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
                bottomSheetActionSong.setSong(data);
                bottomSheetActionSong.show(getChildFragmentManager(), "Action song");
            }
        });

        binding.containerRecentPlaying.setLayoutManager(layoutManager);
        binding.containerRecentPlaying.setAdapter(searchListAdapter);


        return view;
    }

    private void initUser() {
        if( requireActivity() instanceof  HomeActivity ) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();
            initData();

            if( user == null ) return;
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
        ApiService.ApiService.getUserHistory(user.getId()).enqueue(new Callback<ResponseData<List<Song>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Song>>> call, Response<ResponseData<List<Song>>> response) {
                loadingDialog.stopLoading();
                if( !response.isSuccessful()) return;

                ResponseData<List<Song>> data = response.body();

                assert data != null;
                List<Song> histories = data.getData();

                songs.addAll(histories);
                searchListAdapter.notifyDataSetChanged();

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
        super.onDestroy();
    }
}