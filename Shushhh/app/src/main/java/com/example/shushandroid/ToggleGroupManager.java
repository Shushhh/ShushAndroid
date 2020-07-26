package com.example.shushandroid;

import android.view.View;

import com.google.android.material.button.MaterialButton;

/**
 * @apiNote Day of Week Toggle helper class
 * @author  Akash Veerappn
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class ToggleGroupManager {

    private MaterialButton sundayButton;
    private MaterialButton mondayButton;
    private MaterialButton tuesDayButton;
    private MaterialButton wednesdayButton;
    private MaterialButton thursdayButton;
    private MaterialButton fridayButton;
    private MaterialButton saturdayButton;

    /**
     *
     * @param view
     */
    public ToggleGroupManager (View view) {
        sundayButton = view.findViewById(R.id.sunday);
        mondayButton = view.findViewById(R.id.monday);
        tuesDayButton = view.findViewById(R.id.tuesday);
        wednesdayButton = view.findViewById(R.id.wednesday);
        thursdayButton = view.findViewById(R.id.thursday);
        fridayButton = view.findViewById(R.id.friday);
        saturdayButton = view.findViewById(R.id.saturday);
    }

    /**
     *
     * @return
     */
    public String getToggleStateString () {
        String checkedStateString = "";
        if (sundayButton.isChecked()) {
            checkedStateString += "S";
        }
        if (mondayButton.isChecked()) {
            checkedStateString += "M";
        }
        if (tuesDayButton.isChecked()) {
            checkedStateString += "T";
        }
        if (wednesdayButton.isChecked()) {
            checkedStateString += "W";
        }
        if (thursdayButton.isChecked()) {
            checkedStateString += "R";
        }
        if (fridayButton.isChecked()) {
            checkedStateString += "F";
        }
        if (saturdayButton.isChecked()) {
            checkedStateString += "S";
        }
        return checkedStateString;
    }



}
