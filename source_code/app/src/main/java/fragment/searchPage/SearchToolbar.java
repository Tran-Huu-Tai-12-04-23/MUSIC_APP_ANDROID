package fragment.searchPage;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchToolbar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchToolbar extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchToolbar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchToolbar.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchToolbar newInstance(String param1, String param2) {
        SearchToolbar fragment = new SearchToolbar();
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
        View view =  inflater.inflate(R.layout.fragment_search_toolbar, container, false);

        TextInputLayout textInputLayoutSearch = view.findViewById(R.id.input_search);
        EditText inputSearch = textInputLayoutSearch.getEditText();

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
                    sendStateFocus(false);
                }else {
                    sendStateFocus(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void sendStateFocus(boolean hasFocus) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).onInputFocusChanged(hasFocus);
        }
    }
}