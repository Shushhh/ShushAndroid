package com.example.shushandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
/**
 * @apiNote Location Dialog class
 * @author  Sahil Sudhir
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class LocationDialog extends DialogFragment {

    public static class LocationDataTransferItem {
        public static String DATA = "";
    }

    TextView mapText;

    int counter = 0;

    public LocationDialog() {

    }

    /**
     * @param savedInstanceState
     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    /**
     * method that creates the view that contains the dialog
     * @param inflater the layout inflater that opens the layout
     * @param container the view that the UI should attach to
     * @param savedInstanceState creates or reuses the instance state of the layout
     * @return the view shown on screen
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.location_dialog, container, false);
        ImageButton close = view.findViewById(R.id.fullscreen_dialog_close);
        Button action = view.findViewById(R.id.fullscreen_dialog_action);
        Spinner spinner = view.findViewById(R.id.radiusSpinner);

        List<String> radiusList = new ArrayList<>();
        radiusList.add("10m");
        radiusList.add("100m");
        radiusList.add("1000m");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, radiusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //On-Click Listener for Map Not Working
        mapText = view.findViewById(R.id.location);
        mapText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });

        close.setOnClickListener(v -> {
            dismiss();
        });

        action.setOnClickListener(v -> {
            dismiss();
        });

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Lifecycle", "Pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        counter++;
        Log.i("Lifecycle", "Resume");
        if (counter > 1) {
            if (!LocationDataTransferItem.DATA.isEmpty()) {
                mapText.setText(LocationDataTransferItem.DATA);
            }
        }
    }
}
