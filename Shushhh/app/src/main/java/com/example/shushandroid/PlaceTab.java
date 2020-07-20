package com.example.shushandroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PlaceTab extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ShushObject> shushObjectArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_tab, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        ShushRecyclerAdapter locationAdapter = new ShushRecyclerAdapter(shushObjectArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(locationAdapter);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shushObjectArrayList = new ArrayList<>();
        shushObjectArrayList.add(new ShushObject("Work", ShushObject.ShushObjectType.Location.getDescription(), "1005 Cameron Bridge Way", "10 mi"));
        shushObjectArrayList.add(new ShushObject("College", ShushObject.ShushObjectType.Location.getDescription(), "1005 Cameron Bridge Way", "10 mi"));
    }
}