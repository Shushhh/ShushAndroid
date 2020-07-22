package com.example.shushandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShushRecyclerAdapter extends RecyclerView.Adapter<ShushRecyclerAdapter.ShushViewHolder> {

    private ArrayList<ShushObject> shushObjectArrayList;

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

    public ShushRecyclerAdapter(ArrayList<ShushObject> locationList){
        this.shushObjectArrayList = locationList;
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
        ShushObject currentItem = shushObjectArrayList.get(position);
        holder.nameTextView.setText(currentItem.getName());
        holder.dataTextView.setText(currentItem.getData());
        holder.supplementalDataTextView.setText(currentItem.getSupplementalData());
    }

    @Override
    public int getItemCount() {
        return shushObjectArrayList.size();
    }

    public void setShushObjectArrayList(ArrayList<ShushObject> shushObjectArrayList) {
        this.shushObjectArrayList = shushObjectArrayList;
    }
}
