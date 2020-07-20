package com.example.shushandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private ArrayList<LocationRecyclerViewItem> locationList;

    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        public TextView text1;
        public TextView text2;
        public TextView text3;
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.titletext);
            text2 = itemView.findViewById(R.id.placetext);
            text3 = itemView.findViewById(R.id.distancetext);
        }
    }

    public LocationAdapter(ArrayList<LocationRecyclerViewItem> locationList){
        this.locationList = locationList;

    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        LocationViewHolder locationViewHolder = new LocationViewHolder(v);
        return locationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationRecyclerViewItem currentItem = locationList.get(position);
        holder.text1.setText(currentItem.getText1());
        holder.text2.setText(currentItem.getText2());
        holder.text3.setText(currentItem.getText3());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
