package fragment.mainApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.musicplayer.R;

import adapter.HandleListeningItemClicked;
import adapter.PlaylistAdapter;
import data.FakeData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistSuggest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistSuggest extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlaylistSuggest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistSuggest.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistSuggest newInstance(String param1, String param2) {
        PlaylistSuggest fragment = new PlaylistSuggest();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist_suggest, container, false);


//        render data

        RecyclerView playlistSlide = view.findViewById(R.id.playlist_item_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(getContext(),  FakeData.getListImg());

        playlistAdapter.setOnItemClickListener(new HandleListeningItemClicked() {
            @Override
            public void onClick(ImageView imageView, String url) {

            }
        });

        playlistSlide.setLayoutManager(layoutManager);
        playlistSlide.setAdapter(playlistAdapter);

        return view;
    }
}