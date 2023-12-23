package Fragment.SearchPage;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Adapter.SearchListAdapter;
import Adapter.SearchListHistoryAdapter;
import BottomSheet.BottomSheetActionSong;
import BottomSheet.BottomSheetPlayMusic;
import Interface.HandleListeningItemClicked;
import Interface.HandleListeningItemLongClicked;
import Interface.HandleListeningOpenMenuClicked;
import Interface.HandleListeningRemove;
import LocalData.Database.AppDatabase;
import Model.Song;
import DTO.ResponseData;
import Service.ApiService;
import Service.ModelRequest.HistoryRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {
    private LoadingDialog loadingDialog;

    private RecyclerView contentSearchRecyclerview, listItemSearchHistory;
    private LinearLayout contentHistory;
    private boolean isLoading, isLastPage;
    private String key;
    private int currentPage ;
    private ArrayList<Song> songs;
    private SearchListAdapter searchListAdapter;
    private SearchListHistoryAdapter recentSearchHistoryAdapter;
    private TextView recentTitleSearchHistory;
    private LinearLayout noResultNotification;
    private CircularProgressIndicator loadingMore;
    private LinearLayout mainSearchScreen;
    private MaterialButton btnLoadMore,  btnClearHistory;
    private AppDatabase dbLocal ;
    private ArrayList<Song> searchHistories;
    private long userId = -1;


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
        loadingDialog = new LoadingDialog(requireActivity());
        SharedPreferences preferences = requireContext().getSharedPreferences("LoginData", MODE_PRIVATE);
        String strUserId = preferences.getString("userId", "");
        if( !strUserId.equals("")) {
            userId = Long.parseLong(strUserId);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen_search, container, false);
        currentPage = 0;
        songs = new ArrayList<>();
        searchHistories = new ArrayList<>();
        contentSearchRecyclerview = view.findViewById(R.id.content_search);
        contentHistory = view.findViewById(R.id.content_history_search);
        mainSearchScreen = view.findViewById(R.id.main_search);
        btnClearHistory = view.findViewById(R.id.btn_clear_history);
        TextInputLayout inputSearchContainer= view.findViewById(R.id.input_search);
        EditText inputSearch = inputSearchContainer.getEditText();
        TextView tvKeySearch = view.findViewById(R.id.tv_key_search);
        loadingMore = view.findViewById(R.id.loading_more);
        noResultNotification = view.findViewById(R.id.song_result_no_notification);
        btnLoadMore = view.findViewById(R.id.btn_load_more);
        listItemSearchHistory = view.findViewById(R.id.list_item_search_history);

        //hadnle hide when start app
        assert inputSearch != null;
        inputSearch.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            openSoftKeyboard(requireContext(), inputSearch);
        }
//       Handle search
        inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            ) {
                currentPage = 0;
                songs.clear();
                key = inputSearch.getText().toString().trim();
                tvKeySearch.setText(key);
                if( inputSearch.getText().toString().trim().equals("")) {
                    contentHistory.setVisibility(View.VISIBLE);
                    mainSearchScreen.setVisibility(View.GONE);
                }else {
                    contentHistory.setVisibility(View.GONE);
                    loadingMore.setVisibility(View.VISIBLE);
                    mainSearchScreen.setVisibility(View.VISIBLE);
                    searchSong(key);
                }
                return true;
            }
            return false;
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputSearch.getText().toString().equals("")) {
                    if( inputSearch.getText().toString().trim().equals("")) {
                        contentHistory.setVisibility(View.VISIBLE);
                        mainSearchScreen.setVisibility(View.GONE);
                    }else {
                        contentHistory.setVisibility(View.GONE);
                        loadingMore.setVisibility(View.VISIBLE);
                        mainSearchScreen.setVisibility(View.VISIBLE);
                        searchSong(key);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnLoadMore.setOnClickListener(v -> {
            loadMoreItem();
        });

        btnClearHistory.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchListAdapter = new SearchListAdapter(getContext(),  songs);
        contentSearchRecyclerview.setLayoutManager(layoutManager);
        contentSearchRecyclerview.setAdapter(searchListAdapter);
        searchListAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                saveSearchHistory(data);
                searchClick(data);
            }
        });


        searchListAdapter.setOnOpenMenuListener(new HandleListeningOpenMenuClicked<Song>() {
            @Override
            public void onOpenMenu(Song data) {
                openMenu(data);
            }
        });

        //int view recent history
        LinearLayoutManager layoutManagerRecentHistory = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentSearchHistoryAdapter = new SearchListHistoryAdapter(getContext(), searchHistories);
        listItemSearchHistory.setLayoutManager(layoutManagerRecentHistory);
        listItemSearchHistory.setAdapter(recentSearchHistoryAdapter);
        recentSearchHistoryAdapter.setOnItemClickListener(new HandleListeningItemClicked<Song>() {
            @Override
            public void onClick(Song data) {
                searchClick(data);
            }
        });

        recentSearchHistoryAdapter.setOnItemLongClickListener(new HandleListeningItemLongClicked<Song>() {
            @Override
            public void onLongClick(Song data) {
                openMenu(data);
            }
        });
        recentSearchHistoryAdapter.setOnRemoveItemClick(new HandleListeningRemove<Song>() {
            @Override
            public void onRemove(Song data) {
                removeHistory(data);
            }
        });

        initSearchHistory();
        /// handle scroll
        return view;
    }

    private void removeHistory(Song song) {
        loadingDialog.startLoading();
        HistoryRequest historyRequest = new HistoryRequest();
        historyRequest.setSongId(song.getId());
        historyRequest.setUserId((int) userId);
        ApiService.ApiService.removeHistory(historyRequest).enqueue(new Callback<Boolean>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( !response.isSuccessful() ) return ;
                List<Song> filteredList = searchHistories.stream()
                        .filter(dt -> dt.getId() != song.getId())
                        .collect(Collectors.toList());

                searchHistories.clear();
                searchHistories.addAll(filteredList);
                recentSearchHistoryAdapter.notifyDataSetChanged();
                loadingDialog.stopLoading();

                if( searchHistories.size() == 0 ) {
                    btnClearHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                loadingDialog.stopLoading();
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void searchClick(Song data) {
        BottomSheetPlayMusic bottomSheetPlayMusic = new BottomSheetPlayMusic();
        bottomSheetPlayMusic.setSong(data);
        bottomSheetPlayMusic.show(getChildFragmentManager(), "Open playmusic");
    }

    private void openMenu(Song data) {
        BottomSheetActionSong bottomSheetActionSong = new BottomSheetActionSong();
        bottomSheetActionSong.setSong(data);
        bottomSheetActionSong.show(getChildFragmentManager(), "Open action song");
    }

    @SuppressLint("StaticFieldLeak")
    private void saveSearchHistory(Song song) {
        HistoryRequest historyRequest = new HistoryRequest();
        historyRequest.setSongId(song.getId());
        historyRequest.setUserId((int) userId);

        ApiService.ApiService.saveHistory(historyRequest).enqueue(new Callback<ResponseData<Song>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<Song>> call, Response<ResponseData<Song>> response) {
                if( response.isSuccessful() ) {
                    assert response.body() != null;
                    initSearchHistory();
                }

            }

            @Override
            public void onFailure(Call<ResponseData<Song>> call, Throwable t) {
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void initSearchHistory() {
        loadingDialog.startLoading();
        ApiService.ApiService.getUserHistory(userId).enqueue(new Callback<ResponseData<List<Song>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseData<List<Song>>> call, Response<ResponseData<List<Song>>> response) {
                if( response.isSuccessful() ) {
                    assert response.body() != null;
                    searchHistories.clear();
                    searchHistories.addAll(response.body().getData()) ;
                    recentSearchHistoryAdapter.notifyDataSetChanged();
                }

                if( searchHistories.size() == 0 ) {
                    btnClearHistory.setVisibility(View.GONE);
                }else {
                    btnClearHistory.setVisibility(View.VISIBLE);
                }

                Log.i("Search length his ", String.valueOf(searchHistories.size()));

                loadingDialog.stopLoading();
            }

            @Override
            public void onFailure(Call<ResponseData<List<Song>>> call, Throwable t) {
                loadingDialog.stopLoading();
            }
        });


        btnClearHistory.setOnClickListener(v -> {
            removeAllHistory();
        });
    }

    private void removeAllHistory() {
        loadingDialog.startLoading();
        ApiService.ApiService.removeAllHistoryByUserID((int) userId).enqueue(new Callback<Boolean>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if( response.isSuccessful()) {
                    searchHistories.clear();
                    recentSearchHistoryAdapter.notifyDataSetChanged();
                    btnClearHistory.setVisibility(View.GONE);
                }

                loadingDialog.stopLoading();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                loadingDialog.stopLoading();
            }
        });
    }

    private void loadMoreItem() {
        currentPage ++;
        isLoading = true;
        searchSong(key);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void searchSong(String key) {
        loadingMore.setVisibility(View.VISIBLE);
        ApiService.ApiService.searchSong(currentPage, key).enqueue(new Callback<List<Song>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                loadingMore.setVisibility(View.GONE);
                if( !response.isSuccessful()) {
                    return;
                }
                List<Song> data = response.body();
                assert data != null;
                songs.addAll(data);
                searchListAdapter.notifyDataSetChanged();
                isLoading = false;


                if( searchListAdapter.getItemCount()<=0 ){
                    noResultNotification.setVisibility(View.VISIBLE);
                }else {
                    noResultNotification.setVisibility(View.GONE);
                }

                if( songs.size() == 0 ) {
                    btnLoadMore.setVisibility(View.GONE);
                }else {
                    btnLoadMore.setVisibility(View.VISIBLE);
                }

                if (data.size() < 12) {
                    isLastPage = true;
                    btnLoadMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                loadingMore.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}