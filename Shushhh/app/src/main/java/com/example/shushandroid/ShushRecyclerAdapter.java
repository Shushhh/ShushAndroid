package com.example.shushandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShushRecyclerAdapter extends RecyclerView.Adapter<ShushRecyclerAdapter.ShushViewHolder> {

    private ArrayList<LocationRecyclerViewItem> locationList;

    public static class ShushViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView dataTextView;
        public TextView supplementalDataTextView;

        public ShushViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.titletext);
            dataTextView = itemView.findViewById(R.id.placetext);
            supplementalDataTextView = itemView.findViewById(R.id.distancetext);
        }
    }

    public ShushRecyclerAdapter(ArrayList<LocationRecyclerViewItem> locationList){
        this.locationList = locationList;

    }

    @NonNull
    @Override
    public ShushViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ShushViewHolder locationViewHolder = new ShushViewHolder(v);
        return locationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShushViewHolder holder, int position) {
        LocationRecyclerViewItem currentItem = locationList.get(position);
        holder.nameTextView.setText(currentItem.getText1());
        holder.dataTextView.setText(currentItem.getText2());
        holder.supplementalDataTextView.setText(currentItem.getText3());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
