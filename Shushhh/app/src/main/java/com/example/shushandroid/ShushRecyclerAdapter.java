package com.example.shushandroid;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @apiNote Shush Recycler helper class
 * @author  Akash Veerappan
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class ShushRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ShushObject> shushObjectArrayList;
    private ShushDialog timeDialog;
    private FragmentManager fragmentManager;

    private static final int SINGLE_TYPE = 0;
    private static final int DOUBLE_TYPE = 1;

    public static class SingleViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView dataTextView;
        public TextView supplementalDataTextView;
        private TextView repTextView;
        public ConstraintLayout containerView;

        /**
         *
         * @param itemView
         */
        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.titletext);
            dataTextView = itemView.findViewById(R.id.timeText);
            supplementalDataTextView = itemView.findViewById(R.id.dateRepText);
            repTextView = itemView.findViewById(R.id.repTextView);
            containerView = itemView.findViewById(R.id.containerView);
        }
    }


    public static class DoubleViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView timeTextView;
        private TextView dateTextView;
        private TextView locationTextView;
        private TextView radiusTextView;
        private TextView repTextView;
        private ConstraintLayout containerView;

        public DoubleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.titletext);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            repTextView = itemView.findViewById(R.id.repTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            radiusTextView = itemView.findViewById(R.id.radiusTextView);
            containerView = itemView.findViewById(R.id.containerView);
        }
    }


    /**
     *
     * @param shushObjectArrayList
     * @param fragmentManager
     */

    public ShushRecyclerAdapter(ArrayList<ShushObject> shushObjectArrayList, FragmentManager fragmentManager){
        this.shushObjectArrayList = shushObjectArrayList;
        this.fragmentManager = fragmentManager;
        timeDialog = new ShushDialog();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SINGLE_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_single, parent, false);
            return new SingleViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_double, parent, false);
            return new DoubleViewHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == SINGLE_TYPE) {
            ShushObject currentItem = shushObjectArrayList.get(position);
            SingleViewHolder singleViewHolder = (SingleViewHolder) holder;

            if (currentItem.getLocation().equals("N/A")) {
                singleViewHolder.nameTextView.setText(currentItem.getName());
                singleViewHolder.dataTextView.setText(truncateText(currentItem.getTime()));
                singleViewHolder.supplementalDataTextView.setText(currentItem.getDate());
                singleViewHolder.repTextView.setText(currentItem.getRep());
                singleViewHolder.containerView.setOnClickListener(view -> {
                    Bundle bundle = new Bundle(); // send data from this viewHolder to the the timeDialog via a bundle and preset string key constants
                    bundle.putString(DatabaseManager.DatabaseEntry.UUID, currentItem.getUUID());
                    timeDialog.setArguments(bundle);
                    if (timeDialog.isAdded())
                        return;
                    timeDialog.show(fragmentManager,"");
                });
            } else {
                singleViewHolder.nameTextView.setText(currentItem.getName());
                singleViewHolder.dataTextView.setText(truncateText(currentItem.getLocation()));
                singleViewHolder.repTextView.setText(currentItem.getRep());
                singleViewHolder.supplementalDataTextView.setText(currentItem.getRadius());
                singleViewHolder.containerView.setOnClickListener(view -> {
                    Bundle bundle = new Bundle(); // send data from this viewHolder to the the timeDialog via a bundle and preset string key constants
                    bundle.putString(DatabaseManager.DatabaseEntry.UUID, currentItem.getUUID());
                    timeDialog.setArguments(bundle);
                    if (timeDialog.isAdded())
                        return;
                    timeDialog.show(fragmentManager, "");
                });
            }

        } else if (getItemViewType(position) == DOUBLE_TYPE) {
            ShushObject currentItem = shushObjectArrayList.get(position);
            DoubleViewHolder doubleViewHolder = (DoubleViewHolder) holder;

            doubleViewHolder.nameTextView.setText(currentItem.getName());
            doubleViewHolder.timeTextView.setText(currentItem.getTime());
            doubleViewHolder.dateTextView.setText(currentItem.getDate());
            doubleViewHolder.repTextView.setText(currentItem.getRep());
            doubleViewHolder.locationTextView.setText(truncateText(currentItem.getLocation()));
            doubleViewHolder.radiusTextView.setText(currentItem.getRadius());

            doubleViewHolder.containerView.setOnClickListener(view -> {
                Bundle bundle = new Bundle(); // send data from this viewHolder to the the timeDialog via a bundle and preset string key constants
                bundle.putString(DatabaseManager.DatabaseEntry.UUID, currentItem.getUUID());
                timeDialog.setArguments(bundle);
                if (timeDialog.isAdded())
                    return;
                timeDialog.show(fragmentManager, "");
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("List", position + " " + shushObjectArrayList.get(position));
        if (shushObjectArrayList.get(position).getLocation().equals("N/A")|| shushObjectArrayList.get(position).getTime().equals("N/A - N/A")) {
            return SINGLE_TYPE;
        } else {
            Log.i("List", shushObjectArrayList.get(position).toString());
            return DOUBLE_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return shushObjectArrayList.size();
    }

    public void setShushObjectArrayList(ArrayList<ShushObject> shushObjectArrayList) {
        this.shushObjectArrayList = shushObjectArrayList;
    }

    public String truncateText (String longText) {
        if (longText.length() > 30) {
            return longText.substring(0, 25) + "...";
        } else return longText;
    }

}
