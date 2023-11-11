package fragment.searchPage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import adapter.HandleListeningItemClicked;
import adapter.PlaylistAdapter;
import adapter.SearchListAdapter;
import data.FakeData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {

    private RecyclerView contentSearchRecyclerview;
    private TextView recentTitleSearchHistory;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
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
        View view = inflater.inflate(R.layout.fragment_main_screen_search, container, false);

        contentSearchRecyclerview = view.findViewById(R.id.content_search);
        recentTitleSearchHistory = view.findViewById(R.id.title_search_history);
        MaterialButton btnClearHistory = view.findViewById(R.id.btn_clear_history);
        TextInputLayout inputSearchContainer= view.findViewById(R.id.input_search);
        EditText inputSearch = inputSearchContainer.getEditText();

        //hadnle hide when start app
        contentSearchRecyclerview.setVisibility(View.GONE);

//       Handle search

        inputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( inputSearch.getText().toString().trim().equals("")) {
                    contentSearchRecyclerview.setVisibility(View.GONE);
                    recentTitleSearchHistory.setVisibility(View.VISIBLE);
                }else {
                    contentSearchRecyclerview.setVisibility(View.VISIBLE);
                    recentTitleSearchHistory.setVisibility(View.GONE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        recentTitleSearchHistory.setText("No recent search history....");
        btnClearHistory.setVisibility(View.GONE);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SearchListAdapter searchListAdapter = new SearchListAdapter(getContext(),  FakeData.getListImg());

        searchListAdapter.setOnItemClickListener(new HandleListeningItemClicked() {
            @Override
            public void onClick(ImageView imageView, String url) {

            }
        });

        contentSearchRecyclerview.setLayoutManager(layoutManager);
        contentSearchRecyclerview.setAdapter(searchListAdapter);
//

        return view;
    }

}