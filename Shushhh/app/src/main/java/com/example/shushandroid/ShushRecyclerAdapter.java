package com.example.shushandroid;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShushRecyclerAdapter extends RecyclerView.Adapter<ShushRecyclerAdapter.ShushViewHolder> {

    private ArrayList<ShushObject> shushObjectArrayList;
    private TimeDialog timeDialog;
    private FragmentManager fragmentManager;

    public static class ShushViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView dataTextView;
        public TextView supplementalDataTextView;
        public ConstraintLayout containerView;

        public ShushViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.titletext);
            dataTextView = itemView.findViewById(R.id.placetext);
            supplementalDataTextView = itemView.findViewById(R.id.distancetext);
            containerView = itemView.findViewById(R.id.containerView);
        }
    }

    public ShushRecyclerAdapter(ArrayList<ShushObject> locationList, FragmentManager fragmentManager){
        this.shushObjectArrayList = locationList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ShushViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ShushViewHolder locationViewHolder = new ShushViewHolder(view);
        timeDialog = new TimeDialog();
        return locationViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ShushViewHolder holder, int position) {
        ShushObject currentItem = shushObjectArrayList.get(position);

        holder.nameTextView.setText(currentItem.getName());
        holder.dataTextView.setText(currentItem.getData());
        holder.supplementalDataTextView.setText(currentItem.getSupplementalData());

        holder.containerView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(DatabaseManager.DatabaseEntry.NAME, currentItem.getName());
            bundle.putString(DatabaseManager.DatabaseEntry.DATA, currentItem.getData());
            bundle.putString(DatabaseManager.DatabaseEntry.SUPP, currentItem.getSupplementalData());
            timeDialog.setArguments(bundle);
            timeDialog.setArguments(bundle);
            timeDialog.show(fragmentManager, "tag", "click");
        });

    }

    @Override
    public int getItemCount() {
        return shushObjectArrayList.size();
    }

    public void setShushObjectArrayList(ArrayList<ShushObject> shushObjectArrayList) {
        this.shushObjectArrayList = shushObjectArrayList;
    }
}
