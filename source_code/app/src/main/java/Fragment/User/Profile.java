package Fragment.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.example.musicplayer.activity.User.ChangePassword;
import com.example.musicplayer.activity.User.EditProfile;
import com.example.musicplayer.activity.User.StudioUser;
import com.example.musicplayer.activity.User.UploadNewSong;
import com.example.musicplayer.databinding.FragmentProfileBinding;

import BottomSheet.BottomSheetSwitchTheme;
import Model.User;
import utils.LoadingDialog;
import utils.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentProfileBinding binding;
    private User user;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LoadingDialog loadingDialog;
    private Handler handler;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        loadingDialog = new LoadingDialog(requireActivity());
        initUser();

        binding.btnLogout.setOnClickListener(v -> {
            loadingDialog.startLoading();
            HomeActivity homeActivity = (HomeActivity) requireActivity();
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.stopLoading();
                    homeActivity.logout();
                }
            }, 2000);

        });

        binding.btnSwitchTheme.setOnClickListener(v -> {
            BottomSheetSwitchTheme bottomSheetSwitchTheme = new BottomSheetSwitchTheme();

            bottomSheetSwitchTheme.show(getChildFragmentManager(), "Open switch theme");
        });

        binding.btnActiveDashboard.setOnClickListener(v -> {
            if( user == null ) return;
            Intent intent = new Intent(requireContext(), StudioUser.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", this.user);
            intent.putExtra("user", bundle);
            startActivity(intent);
        });

        binding.btnUploadSong.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UploadNewSong.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", this.user);
            intent.putExtra("user", bundle);
            startActivity(intent);
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePassword.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtra("user", bundle);
            startActivity(intent);
        });

        binding.btnEditProfile.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            Intent intent = new Intent(requireContext(), EditProfile.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtra("user", bundle);
            startActivity(intent);
        });


        return view;
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
            binding.tvUsername.setText(user.getUsername());
        }
    }
    @Override
    public void onDestroy() {
        Log.i("Destroy tag", "test");
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        if( loadingDialog != null ) {
            loadingDialog.stopLoading();
        }
    }
}