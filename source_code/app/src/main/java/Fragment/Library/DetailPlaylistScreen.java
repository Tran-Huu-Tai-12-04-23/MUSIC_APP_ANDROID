package Fragment.Library;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.databinding.FragmentDetailPlaylistScreenBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Adapter.ItemDetailPlaylistAdapter;
import BottomSheet.BottomSheetActionSong;
import DTO.ResponseData;
import Interface.HandleListeningItemClicked;
import Model.DetailPlaylist;
import Model.Playlist;
import Model.User;
import Service.ApiService;
import Service.PlayMusicService;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailPlaylistScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailPlaylistScreen extends Fragment {
    private FragmentDetailPlaylistScreenBinding binding;
    private ItemDetailPlaylistAdapter itemDetailPlaylistAdapter;
    private ArrayList<DetailPlaylist> detailPlaylists;
    private User user;
    private Playlist playlist;
    private LoadingDialog loadingDialog ;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DetailPlaylistScreen() {
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
    public static DetailPlaylistScreen newInstance(String param1, String param2) {
        DetailPlaylistScreen fragment = new DetailPlaylistScreen();
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
        binding = FragmentDetailPlaylistScreenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        detailPlaylists = new ArrayList<>();
        loadingDialog = new LoadingDialog(requireActivity());
        Bundle args = getArguments();

        if (args != null && args.containsKey("playlist")) {
            // Retrieve the Playlist object
            playlist = (Playlist) args.getSerializable("playlist");
            Toast.makeText(getContext(), playlist.getTitle(), Toast.LENGTH_SHORT).show();
        }

        initUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        itemDetailPlaylistAdapter = new ItemDetailPlaylistAdapter(getContext(),  detailPlaylists);

        itemDetailPlaylistAdapter.setOnItemClickListener(new HandleListeningItemClicked<DetailPlaylist>() {
            @Override
            public void onClick(DetailPlaylist data) {
                PlayMusicService.playMusic(requireContext(), data.getSong());
            }
        });

        itemDetailPlaylistAdapter.setOnItemLongClickListener(data -> {
            BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
            bottomSheetActionSong.setSong(data.getSong());
            bottomSheetActionSong.show(getChildFragmentManager(), "Action song");
        });

        binding.contentMusicInPlaylist.setLayoutManager(layoutManager);
        binding.contentMusicInPlaylist.setAdapter(itemDetailPlaylistAdapter);

        binding.btnBack.setOnClickListener(v -> {
            if( requireActivity() instanceof HomeActivity ) {
                getChildFragmentManager().popBackStack();
            }
        });


        initData();
        if(Objects.equals(user.getId(), playlist.getUser().getId())) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(binding.contentMusicInPlaylist);
        }



        return view;
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
            if( detailPlaylists.size() <=  postitionCom ) return;
            DetailPlaylist detailPlaylist = detailPlaylists.get(postitionCom);

            if( detailPlaylist == null) return;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isCurrentlyActive) {
                if (Math.abs(dX) >= 500) {
                    removeDetailPlaylist(detailPlaylist);
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
    private void removeDetailPlaylist(DetailPlaylist detailPlaylist) {

        if (detailPlaylist == null) return;

        ArrayList<DetailPlaylist> prevArraylistDetailPlaysList = new ArrayList<>(detailPlaylists);
        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                destroyPlaylist(detailPlaylist);
            }
        }.start();

        if(detailPlaylists.isEmpty()) return;
        detailPlaylists.removeIf(p -> Objects.equals(p.getId(), detailPlaylist.getId()));
        itemDetailPlaylistAdapter.notifyDataSetChanged();
        Snackbar.make(requireView(), "Đã xóa bài hát khỏi danh sách phát", Snackbar.LENGTH_LONG)
                .setAction("Hoàn tác", new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        detailPlaylists.clear();
                        detailPlaylists.addAll(prevArraylistDetailPlaysList);
                        itemDetailPlaylistAdapter.notifyDataSetChanged();
                    }
                })
                .show();


    }

    private void destroyPlaylist(DetailPlaylist detailPlaylists) {
        ApiService.ApiService.removeSongFromPlaylist((long) detailPlaylists.getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i("remove detail playlist", t.getLocalizedMessage());
            }
        });
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

        }
    }

    private void initData() {
        if( playlist == null ) return;

        binding.tvNamePlaylist.setText(playlist.getTitle());
        Glide.with(requireContext())
                .load(playlist.getThumbnails())
                .placeholder(R.drawable.baseline_person_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.thumbnailsPlaylist);

        loadingDialog.startLoading();

        ApiService.ApiService.getDetailPlaylist(playlist.getId()).enqueue(new Callback<ResponseData<List<DetailPlaylist>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<DetailPlaylist>>> call, Response<ResponseData<List<DetailPlaylist>>> response) {
                loadingDialog.stopLoading();

                if(response.isSuccessful()) {
                    ResponseData<List<DetailPlaylist>>  res = response.body();
                    List<DetailPlaylist> detailPlaylistsRes = res.getData();

                    if( detailPlaylistsRes == null ) return;

                    detailPlaylists.addAll(detailPlaylistsRes);
                    itemDetailPlaylistAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DetailPlaylist>>> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("TAG", t.getLocalizedMessage());
            }
        });
    }
}