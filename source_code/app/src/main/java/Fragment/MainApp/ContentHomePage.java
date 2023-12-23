package Fragment.MainApp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.FragmentContentHomePageBinding;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import Adapter.ArtistAdapter;
import Adapter.ItemCarouselAdapter;
import Adapter.MusicItemAdapter;
import Adapter.PlaylistHomeAdapter;
import BottomSheet.BottomSheetActionArtist;
import BottomSheet.BottomSheetActionPlaylist;
import BottomSheet.BottomSheetActionSong;
import DTO.HomeResponse;
import Interface.BottomSheetActionArtistInteractionListener;
import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import LocalData.Entity.StatePlayMusic;
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
 * Use the {@link ContentHomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentHomePage extends Fragment implements BottomSheetActionArtistInteractionListener {
    private FragmentContentHomePageBinding binding;
    private User user;
    private ArrayList<User> artists;
    private ArtistAdapter artistAdapter;
    private Long userId;
    LoadingDialog loadingDialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView musicSlide, newMusicContainer, recentMusicContainer, artistContainer, playlistContainer;
    private ViewPager viewPagerTrending;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContentHomePage() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContentHomePage.
     */
    // TODO: Rename and change types and number of parameters
    public static ContentHomePage newInstance(String param1, String param2) {
        ContentHomePage fragment = new ContentHomePage();
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

        binding = FragmentContentHomePageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        loadingDialog = new LoadingDialog(requireActivity());
        artists = new ArrayList<>();
//        viewPagerTrending = view.findViewById(R.id.paper_trending);

        binding.searchBtn.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            homeActivity.openSearchScreen();
        });
        initUser();

        SharedPreferences preferences = requireActivity().getSharedPreferences("LoginData", MODE_PRIVATE);

        String strUserId = preferences.getString("userId", "");
        if( !strUserId.equals("")) {
            userId = Long.parseLong(strUserId);
            initData(userId);
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initUser() {
        if( requireActivity() instanceof HomeActivity ) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();
            if( this.user == null ) return;
            Glide.with(requireContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.baseline_person_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                    .into(binding.avatar);
            binding.tvNameUser.setText(user.getUsername().substring(0,1).toUpperCase() + user.getUsername().substring(1).toLowerCase());
        }
    }
    private void initData(Long userId) {

        loadingDialog.startLoading();
        ApiService.ApiService.getHomePage().enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if( response.isSuccessful()) {
                    HomeResponse homeResponse = response.body();
                    assert homeResponse != null;
                    renderMusicSlide(binding.carouselMusicContainer, homeResponse.getFamousSong());
                    renderRecentMusic(binding.recentMusicHome, homeResponse.getRecentMusic());
                    renderNewMusic(binding.newMusicHome, homeResponse.getNewestSong());
                    renderPlaylistHome(binding.playlistItemHome,homeResponse.getFamousPlaylist());
                    artists = homeResponse.getArtistList().stream()
                            .filter(user -> !Objects.equals(user.getId(), userId))
                            .collect(Collectors.toCollection(ArrayList::new));
                    renderArtistFamous(binding.artistFamousHome,artists);
                }

                loadingDialog.stopLoading();

            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.i("TAG", t.getLocalizedMessage());
                loadingDialog.stopLoading();
            }
        });
    }

    public void renderMusicSlide(RecyclerView musicSlide, ArrayList<Song> data) {
        ItemCarouselAdapter itemCarouselAdapter = new ItemCarouselAdapter(getContext(),data);
        itemCarouselAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                Toast.makeText(getContext(), data.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        musicSlide.setAdapter(itemCarouselAdapter);

    }

    public void renderPlaylistHome(RecyclerView playlistSlide, ArrayList<Playlist> data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        PlaylistHomeAdapter playlistAdapter = new PlaylistHomeAdapter(getContext(),  data);

        playlistAdapter.setOnItemClickListener(new HandleListeningItemClicked<Playlist>() {
            @Override
            public void onClick(Playlist data) {
                if( requireActivity() instanceof  HomeActivity) {
                    HomeActivity homeActivity = (HomeActivity) requireActivity();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("playlist", data);
                    homeActivity.openPlaylistDetail(bundle);
                }
            }
        });

        playlistAdapter.setOnItemLongClickListener(new HandleListeningItemLongClicked<Playlist>() {
            @Override
            public void onLongClick(Playlist data) {
                BottomSheetActionPlaylist bottomSheetActionPlaylist = new BottomSheetActionPlaylist();
                bottomSheetActionPlaylist.setPlaylist(data);
                bottomSheetActionPlaylist.show(getChildFragmentManager(), "PLaylist bottom sheet");
            }
        });

        playlistSlide.setLayoutManager(layoutManager);
        playlistSlide.setAdapter(playlistAdapter);
    }

    public void renderRecentMusic(RecyclerView recentMusic, ArrayList<Song> data) {
        LinearLayoutManager layoutManagerRecentMusic = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        MusicItemAdapter itemRecentMusicAdapter = new MusicItemAdapter(getContext(), data, true);

        itemRecentMusicAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                playMusic(data);
            }
        });

        itemRecentMusicAdapter.setOnItemLongClickListener(new HandleListeningItemLongClicked<Song>() {
            @Override
            public void onLongClick(Song data) {
                BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
                bottomSheetActionSong.setSong(data);
                bottomSheetActionSong.show(getChildFragmentManager(), "Bottom sheet");
            }
        });

        recentMusic.setLayoutManager(layoutManagerRecentMusic);
        recentMusic.setAdapter(itemRecentMusicAdapter);
    }

    public void renderArtistFamous(RecyclerView artistFamousSlide, ArrayList<User> artistListData) {
        LinearLayoutManager layoutManagerFamousArtist = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        artistAdapter = new ArtistAdapter(getContext(), artistListData);

        artistAdapter.setOnItemLongClickListener(new HandleListeningItemLongClicked<User>() {
            @Override
            public void onLongClick(User data) {
                BottomSheetActionArtist bottomSheetActionArtist = new BottomSheetActionArtist();
                User user = new User();
                user.setId(userId);
                bottomSheetActionArtist.setUser(user);
                bottomSheetActionArtist.setUserTarget(data);
                bottomSheetActionArtist.show(getChildFragmentManager(), "Bottom sheet user");

            }
        });
        artistAdapter.setOnItemClickListener(new HandleListeningItemClicked<User>() {
            @Override
            public void onClick(User data) {
                if( requireActivity() instanceof  HomeActivity ) {
                    HomeActivity homeActivity = (HomeActivity) requireActivity();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", data);
                    homeActivity.openDetailArtist(bundle);
                }
            }
        });
        artistFamousSlide.setLayoutManager(layoutManagerFamousArtist);
        artistFamousSlide.setAdapter(artistAdapter);
    }

    public void renderNewMusic(RecyclerView newMusicContainer, ArrayList<Song> data) {
        LinearLayoutManager layoutManagerNewMusic = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        MusicItemAdapter itemNewMusicAdapter = new MusicItemAdapter(getContext(), data, true);


        itemNewMusicAdapter.setOnItemLongClickListener(new HandleListeningItemLongClicked<Song>() {
            @Override
            public void onLongClick(Song data) {
                BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
                bottomSheetActionSong.setSong(data);
                bottomSheetActionSong.show(getChildFragmentManager(), "Bottom sheet");
            }
        });

        itemNewMusicAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                playMusic(data);
            }
        });

        newMusicContainer.setLayoutManager(layoutManagerNewMusic);
        newMusicContainer.setAdapter(itemNewMusicAdapter);
    }

    private void playMusic(Song song) {
        HomeActivity homeActivity = (HomeActivity) requireActivity();
        Song currentSong = homeActivity.getCurrentSong();
        StatePlayMusic statePlayMusic = homeActivity.getStatePlayMusic();

        if( statePlayMusic != null && currentSong != null) {
            if(  statePlayMusic.isPause()) {
                PlayMusicService.resumeMusic(requireContext());
            }else if( currentSong.getId() != song.getId() ) {
                PlayMusicService.playMusic(requireContext(), song);
            }
        } else if( currentSong != null ) {
            if(currentSong.getId() != song.getId()) {
                PlayMusicService.playMusic(requireContext(), song);
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChangeUnFollow(User user) {
        artists = (ArrayList<User>) artists.stream()
                .map(us -> {
                    if (Objects.equals(us.getId(), user.getId())) {
                        us.setTotalFollow(us.getTotalFollow() - 1);
                    }
                    return us;
                })
                .collect(Collectors.toList());

        artistAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChangeFollow(User user) {
        artists = (ArrayList<User>) artists.stream()
                .map(us -> {
                    if (Objects.equals(us.getId(), user.getId())) {
                        us.setTotalFollow(us.getTotalFollow() + 1);
                    }
                    return us;
                })
                .collect(Collectors.toList());

        artistAdapter.notifyDataSetChanged();
    }

}