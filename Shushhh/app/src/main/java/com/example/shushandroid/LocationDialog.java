package com.example.shushandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @apiNote Location Dialog helper class
 * @author  Sahil Sudhir
 * @version 1.0
 * @since   2020-7-25
 *
 * Resources:
 */

public class LocationDialog extends DialogFragment {

    /**
     * method that returns a new instance of the LocationDialog class
     * @return a LocationDialog object instance
     */
    static LocationDialog newInstance () {
        return new LocationDialog();
    }

    /**
     * method that creates the dialog
     * @param savedInstanceState a bundle that contains the necessary items to create an instance of the dialog
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

        close.setOnClickListener(v -> {
            dismiss();
        });
        action.setOnClickListener(v -> {
            dismiss();
        });

        return view;

    }
}
