package com.example.shushandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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

    TextView mapText;

    private MainActivity context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = (MainActivity) context;
    }


    public LocationDialog() {

    }


    /**
     *
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

        //On-Click Listener for Map Not Working
        mapText = view.findViewById(R.id.location);
        mapText.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapActivity.class);
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
}
