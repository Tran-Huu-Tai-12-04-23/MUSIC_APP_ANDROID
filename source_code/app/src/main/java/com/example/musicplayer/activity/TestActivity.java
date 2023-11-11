package com.example.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import adapter.PlaylistAdapter;
import data.FakeData;

public class TestActivity extends AppCompatActivity {

    SearchBar searchBar;
    SearchView searchView;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



//        searchBar = findViewById(R.id.search_bar);
        searchView = findViewById(R.id.search_view);
        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.list_item_test);

        // Initialize and set up your RecyclerView here with a LinearLayoutManager and an adapter.
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        PlaylistAdapter playlistAdapter = new PlaylistAdapter(getApplicationContext(),  FakeData.getListImg());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(playlistAdapter);
        searchView.hide();
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.show();
            }
        });

        searchView.setupWithSearchBar(searchBar);

        searchView.addTransitionListener(
                (searchView, previousState, newState) -> {
                    Toast.makeText(this, newState.toString(), Toast.LENGTH_SHORT).show();
                    if (newState == SearchView.TransitionState.SHOWING || newState == SearchView.TransitionState.SHOWN) {
                        searchBar.setVisibility(View.GONE);
                    }

                    if( newState == SearchView.TransitionState.HIDDEN) {
                        searchBar.setVisibility(View.VISIBLE);
                    }
                });


        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(TestActivity.this, "Heloo on change", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        searchView
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            searchBar.setText(searchView.getText());
                            searchView.hide();
                            return false;
                        });

    }
}