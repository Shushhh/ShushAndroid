package com.example.shushandroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PlaceTab extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ShushObject> shushObjectArrayList = new ArrayList<>();
    private DatabaseManager databaseManager;
    private ShushRecyclerAdapter shushRecyclerAdapter;

    private static final String TAG = ShushObject.ShushObjectType.LOCATION.getDescription();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_tab, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        shushRecyclerAdapter = new ShushRecyclerAdapter(shushObjectArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new DatabaseManager(getActivity());
    }

    public void setShushObjectArrayList(ArrayList<ShushObject> shushObjectArrayList) {
        this.shushObjectArrayList = shushObjectArrayList;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView () {
        shushObjectArrayList = databaseManager.retrieveWithTAG(TAG);
        shushRecyclerAdapter.setShushObjectArrayList(shushObjectArrayList);
        recyclerView.setAdapter(shushRecyclerAdapter);
    }

}