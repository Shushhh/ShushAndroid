package com.example.shushandroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TimeTab extends Fragment {

    private RecyclerView recyclerView;
    private ShushRecyclerAdapter shushRecyclerAdapter;
    private ArrayList<ShushObject> shushObjectList = new ArrayList<>();
    private DatabaseManager databaseManager;

    private static final String TAG = ShushObject.ShushObjectType.TIME.getDescription();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_tab, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        shushRecyclerAdapter = new ShushRecyclerAdapter(shushObjectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shushObjectList = new ArrayList<>();
        databaseManager = new DatabaseManager(getActivity());
    }

    public void setShushObjectArrayList(ArrayList<ShushObject> shushObjectList) {
        this.shushObjectList = shushObjectList;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView () {
        shushObjectList = databaseManager.retrieveWithTAG(TAG);
        shushRecyclerAdapter.setShushObjectArrayList(shushObjectList);
        recyclerView.setAdapter(shushRecyclerAdapter);
    }

}