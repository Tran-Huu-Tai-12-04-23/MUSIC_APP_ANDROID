package Fragment.Library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.FragmentDetailArtistScreenBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.MusicItemAdapter;
import Adapter.PlaylistAdapter;
import BottomSheet.BottomSheetActionArtist;
import BottomSheet.BottomSheetActionPlaylist;
import BottomSheet.BottomSheetActionSong;
import DTO.ResponseData;
import Model.Follow;
import Model.Playlist;
import Model.Song;
import Model.User;
import Service.ApiService;
import Service.PlayMusicService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailArtistScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailArtistScreen extends Fragment {
    private FragmentDetailArtistScreenBinding binding;
    private PlaylistAdapter playlistAdapter;
    private ArrayList<Song> songs;
    private ArrayList<Playlist> playlists;

    private MusicItemAdapter musicItemAdapter;
    private User user;
    private User userTarget;
    private LoadingDialog loadingDialog ;
    private Boolean isFollowed;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DetailArtistScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailPlaylistScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailArtistScreen newInstance(String param1, String param2) {
        DetailArtistScreen fragment = new DetailArtistScreen();
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
        binding = FragmentDetailArtistScreenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
//        loadingDialog = new LoadingDialog(requireActivity());

        Bundle args = getArguments();

        if (args != null && args.containsKey("user")) {
            // Retrieve the Playlist object
            this.userTarget = (User) args.getSerializable("user");
            initUser();
        }

        if( requireActivity() instanceof  HomeActivity ) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManagerPlaylist = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        playlistAdapter = new PlaylistAdapter(getContext(), playlists);
        musicItemAdapter = new MusicItemAdapter(getContext(), songs, false);

        playlistAdapter.setOnItemClickListener(data -> {
            if(requireActivity() instanceof  HomeActivity ) {
                HomeActivity homeActivity = (HomeActivity) requireActivity();
                Bundle bundle = new Bundle();
                bundle.putSerializable("playlist", data);
                homeActivity.openPlaylistDetail(bundle);
            }
        });

        playlistAdapter.setOnItemLongClickListener(data -> {
            BottomSheetActionPlaylist bottomSheetActionPlaylist = new BottomSheetActionPlaylist();
            bottomSheetActionPlaylist.setPlaylist(data);
            bottomSheetActionPlaylist.show(getChildFragmentManager(), "Action playlist");
        });

        musicItemAdapter.setOnItemClickListener( data -> {
            PlayMusicService.playMusic(requireContext(), data);
        });

        musicItemAdapter.setOnItemLongClickListener( data -> {
            BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
            bottomSheetActionSong.setSong(data);
            bottomSheetActionSong.show(getChildFragmentManager(), "Show action song");
        });

        binding.contentMusic.setLayoutManager(layoutManager);
        binding.contentMusic.setAdapter(musicItemAdapter);
        binding.contentPlaylist.setLayoutManager(layoutManagerPlaylist);
        binding.contentPlaylist.setAdapter(playlistAdapter);

        binding.btnBack.setOnClickListener(v -> {
            if( requireActivity() instanceof HomeActivity ) {
                getChildFragmentManager().popBackStack();
            }
        });

        binding.btnOpenMenu.setOnClickListener(v -> {
            if( user == null || userTarget== null) return;
            BottomSheetActionArtist bottomSheetActionArtist = new BottomSheetActionArtist();
            bottomSheetActionArtist.setUser(user);
            bottomSheetActionArtist.setUserTarget(this.userTarget);
            bottomSheetActionArtist.show(getChildFragmentManager(), "Show action user");
        });


        initDataSongUser();
        initDataPlaylistUser();
        isCheckUserFollow();

        binding.btnFollow.setOnClickListener(v -> {
            if( isFollowed ) {
                handleUnFollow();
            }else {
                handleFollow();
            }
        });


        return view;
    }

    private void isCheckUserFollow() {
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
                    isFollowed = false;
                    handleUpdateUI();
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
                    isFollowed = true;
                    handleUpdateUI();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Playlist>> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());
            }
        });
    }

    private void initDataPlaylistUser() {
        if( this.userTarget == null ) return;

//        loadingDialog.startLoading();
        ApiService.ApiService.getAllPublicPlaylistByUser(userTarget.getId()).enqueue(new Callback<ResponseData<List<Playlist>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Playlist>>> call, Response<ResponseData<List<Playlist>>> response) {
//                loadingDialog.stopLoading();
                if( response.isSuccessful()) {
                    ResponseData<List<Playlist>> res = response.body();
                    if( res == null ) return;
                    playlists.clear();
                    playlists.addAll(res.getData());
                    playlistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<Playlist>>> call, Throwable t) {
//                loadingDialog.stopLoading();
                Log.i("TAG", t.getLocalizedMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initUser() {
        if( this.userTarget == null ) return;

        Glide.with(requireContext())
                .load(userTarget.getAvatar())
                .placeholder(R.drawable.baseline_person_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.avatar);

        binding.tvNameArtist.setText(userTarget.getUsername());
        binding.tvTotalFollower.setText(userTarget.getTotalFollow() + " người theo dõi");
    }

    private void initDataSongUser() {
        if( userTarget == null ) return;

//        loadingDialog.startLoading();
        ApiService.ApiService.getAllPublicSongByUser(userTarget.getId()).enqueue(new Callback<ResponseData<List<Song>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Song>>> call, Response<ResponseData<List<Song>>> response) {
//                loadingDialog.stopLoading();
                if( response.isSuccessful()) {
                    ResponseData<List<Song>> res = response.body();
                    if( res == null ) return;
                    songs.clear();
                    songs.addAll(res.getData());
                    musicItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<Song>>> call, Throwable t) {
//                loadingDialog.stopLoading();
                Log.i("TAG", t.getLocalizedMessage());
            }
        });
    }
}