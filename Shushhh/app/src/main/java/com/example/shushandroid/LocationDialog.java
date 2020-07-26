package com.example.shushandroid;

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

    /**
     *
     * @return
     */
    static LocationDialog newInstance () {
        return new LocationDialog();
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
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
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
