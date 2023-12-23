package Fragment.Library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import Adapter.FollowingAdapter;
import BottomSheet.BottomSheetActionArtist;
import DTO.ResponseData;
import Interface.BottomSheetActionArtistInteractionListener;
import Model.Follow;
import Model.Playlist;
import Model.User;
import Service.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingScreen extends Fragment implements BottomSheetActionArtistInteractionListener {
    private FollowingAdapter followingAdapter;
    private ArrayList<User> users;
    private LoadingDialog loadingDialog;
    private User user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowingScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingScreen newInstance(String param1, String param2) {
        FollowingScreen fragment = new FollowingScreen();
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
        users = new ArrayList<>();
        loadingDialog = new LoadingDialog(requireActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following_screen, container, false);
        RecyclerView containerArtistFollowing ;
        containerArtistFollowing = view.findViewById(R.id.container_artist_followed);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        followingAdapter = new FollowingAdapter(getContext(),  users);
        containerArtistFollowing.setLayoutManager(layoutManager);
        containerArtistFollowing.setAdapter(followingAdapter);

        followingAdapter.setOnClickListener(user -> {

        });

        followingAdapter.setOnLongClickListener(dt -> {
            BottomSheetActionArtist bottomSheetActionArtist = new BottomSheetActionArtist();
            bottomSheetActionArtist.setUser(user);
            bottomSheetActionArtist.setUserTarget(dt);
            bottomSheetActionArtist.show(getChildFragmentManager(), "Show action artist");
        });


        MaterialButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v ->{
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            homeActivity.onBackPressed();
        });

        initUser();

        return view;
    }

    private void initUser() {
        if( requireActivity() instanceof  HomeActivity ) {
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            this.user = homeActivity.getUser();

            initData();
        }
    }

    private void initData() {
        if( user == null ) return;

        loadingDialog.startLoading();

        ApiService.ApiService.getAllFollowee(user.getId()).enqueue(new Callback<List<User>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                loadingDialog.stopLoading();
                if( response.isSuccessful()) {
                    users.clear();
                    if( response.body() == null ) return;
                    users.addAll(response.body());
                    followingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loadingDialog.stopLoading();
                Log.i("TAG", t.getLocalizedMessage());

            }
        });

    }

    @Override
    public void onChangeUnFollow(User user) {
        ArrayList<User> userPrevs = new ArrayList<>(users);
        initData();

        if(user == null ) return;
        Snackbar.make(requireView() , "Hủy theo dõi " + user.getUsername(), Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser(user, userPrevs);
            }
        }).show();
    }

    private void followUser(User us, ArrayList<User> userPrevs) {
        Follow follow = new Follow();
        follow.setFollowee(us);
        follow.setFollower(this.user);
        ApiService.ApiService.follow(follow).enqueue(new Callback<ResponseData<Playlist>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<Playlist>> call, Response<ResponseData<Playlist>> response) {
                if( response.isSuccessful()) {
                    users.clear();
                    users.addAll(userPrevs);
                    followingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Playlist>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onChangeFollow(User user) {

    }
}