package Fragment.Library;

import android.os.Bundle;
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

import Adapter.MusicItemAdapter;
import Adapter.PlaylistAdapter;
import Model.Playlist;
import Model.Song;
import Model.User;
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
    private LoadingDialog loadingDialog ;
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
        loadingDialog = new LoadingDialog(requireActivity());

        initUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        playlistAdapter = new PlaylistAdapter(getContext(), playlists);
        musicItemAdapter = new MusicItemAdapter(getContext(), songs);

        binding.contentMusic.setLayoutManager(layoutManager);
        binding.contentMusic.setAdapter(musicItemAdapter);
        binding.contentPlaylist.setLayoutManager(layoutManager);
        binding.contentPlaylist.setAdapter(musicItemAdapter);

        binding.btnBack.setOnClickListener(v -> {
            if( requireActivity() instanceof HomeActivity ) {
                getChildFragmentManager().popBackStack();
            }
        });


        initData();


        return view;
    }

    private void initUser() {
        if( requireActivity() instanceof  HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();

            if( this.user == null ) return;

            Glide.with(requireContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.baseline_person_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.avatar);

            binding.tvNameArtist.setText(user.getUsername());
            binding.tvTotalFollower.setText(user.getTotalFollow() + " người theo dõi");

        }
    }

    private void initData() {
        if( user == null ) return;

        loadingDialog.startLoading();
//        ApiService.ApiService.
    }
}