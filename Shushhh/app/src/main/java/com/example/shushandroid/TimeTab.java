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

public class TimeTab extends Fragment {

    private RecyclerView recyclerView;
    private ShushRecyclerAdapter shushRecyclerAdapter;
    private ArrayList<ShushObject> shushObjectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_tab, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        shushRecyclerAdapter = new ShushRecyclerAdapter(shushObjectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(shushRecyclerAdapter);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shushObjectList = new ArrayList<>();
        shushObjectList.add(new ShushObject("Studying", ShushObject.ShushObjectType.TIME.getDescription(), "10:00 PM - 11:00 PM", "1 hour"));
    }

    public void setShushObjectArrayList(ArrayList<ShushObject> shushObjectList) {
        this.shushObjectList = shushObjectList;
    }
}