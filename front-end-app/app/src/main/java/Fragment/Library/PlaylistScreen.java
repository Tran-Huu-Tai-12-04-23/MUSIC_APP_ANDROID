package Fragment.Library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.FragmentPlaylistScreenBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Adapter.PlaylistAdapter;
import BottomSheet.BottomSheetAddPlaylist;
import DTO.ResponseData;
import Interface.BottomSheetAddPlaylistInteractionListener;
import Interface.HandleListeningItemClicked;
import Model.Playlist;
import Model.User;
import Service.ApiService;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistScreen extends Fragment implements BottomSheetAddPlaylistInteractionListener {

    FragmentPlaylistScreenBinding binding;
    private User user;
    private ArrayList<Playlist> playlists;
    private PlaylistAdapter playlistAdapter;
    private LoadingDialog loadingDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlaylistScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistScreen newInstance(String param1, String param2) {
        PlaylistScreen fragment = new PlaylistScreen();
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

        binding = FragmentPlaylistScreenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        playlists = new ArrayList<>();
        loadingDialog = new LoadingDialog(requireActivity());

        // Inflate the layout for this fragment
        initUser();
        initDataPlaylistUser();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        playlistAdapter = new PlaylistAdapter(getContext(),  playlists);
        playlistAdapter.setOnItemClickListener(new HandleListeningItemClicked<Playlist>() {
            @Override
            public void onClick(Playlist data) {
                openDetailPlaylist(data);
            }
        });

        binding.containerPlaylistItem.setLayoutManager(layoutManager);
        binding.containerPlaylistItem.setAdapter(playlistAdapter);

        binding.btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddPlaylist(v.getContext());
            }
        });


        binding.btnBack.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            homeActivity.onBackPressed();
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.containerPlaylistItem);


        return view;
    }

    private void openDetailPlaylist(Playlist data) {
        if( requireActivity() instanceof  HomeActivity && data != null ) {
            HomeActivity homeActivity =(HomeActivity) requireActivity();
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist", data);
            homeActivity.openPlaylistDetail(bundle);
        }
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
            if( playlists.size() <=  postitionCom ) return;
            Playlist playlist = playlists.get(postitionCom);
            if( playlist == null) return;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isCurrentlyActive) {
                if (Math.abs(dX) >= 500) {
                    removePlaylist(playlist);
                    super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
                    return;
                }
            }

            if (dX < 0 && dX >= -500 ) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_error))
                        .addActionIcon(R.drawable.trash_bin_2_svgrepo_com)
                        .create()
                        .decorate();
            } else if( dX < -500 ) {
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
    private void removePlaylist(Playlist playlist) {

        if (playlist == null) return;

        ArrayList<Playlist> prevArraylist = new ArrayList<>(playlists);
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                destroyPlaylist(playlist);
            }
        }.start();

        if(playlists.isEmpty()) return;
        playlists.removeIf(p -> Objects.equals(p.getId(), playlist.getId()));
        playlistAdapter.notifyDataSetChanged();
        Snackbar.make(requireView(), "Xóa danh sách phát", Snackbar.LENGTH_LONG)
                .setAction("Hoàn tác", new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        playlists.clear();
                        playlists.addAll(prevArraylist);
                        playlistAdapter.notifyDataSetChanged();
                    }
                })
                .show();


    }

    private void destroyPlaylist(Playlist playlist) {
        ApiService.ApiService.removePlaylistUser(playlist.getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i("remove playlist", t.getLocalizedMessage());
            }
        });
    }


    private void initDataPlaylistUser() {
        HomeActivity homeActivity = (HomeActivity) requireActivity();
        User user = homeActivity.getUser();

        if( user == null ) return;
        initAvatar(user);
        loadingDialog.startLoading();

        ApiService.ApiService.getAllPlaylistByUser(user.getId()).enqueue(new Callback<ResponseData<List<Playlist>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Playlist>>> call, Response<ResponseData<List<Playlist>>> response) {
                loadingDialog.stopLoading();
                if( response.isSuccessful() ) {
                    ResponseData<List<Playlist>> responseData = response.body();
                    assert responseData != null;
                    if( responseData.getData() ==  null ) return;
                    playlists.clear();
                    playlists.addAll(responseData.getData());
                    playlistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<Playlist>>> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("Playlist screeen " , t.getLocalizedMessage());
            }
        });

    }
    private void initAvatar(User user) {
        Glide.with(requireContext())
                .load(user.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.avatar);
    }

    private void showDialogAddPlaylist(Context context) {
        BottomSheetAddPlaylist bottomSheetAddPlaylist = new BottomSheetAddPlaylist();
        bottomSheetAddPlaylist.show(getChildFragmentManager(), "Add playlist");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChangeAddPlaylist(Playlist playlist) {
        playlist.setCountSong(0L);
        playlists.add(playlist);
        playlistAdapter.notifyDataSetChanged();
    }
}