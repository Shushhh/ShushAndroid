package com.example.shushandroid;

import android.view.View;

import com.google.android.material.button.MaterialButton;

/**

 * Efficiently manage toggle button group via this class
 * NOTE: use the same IDs for both locationDialog and timeDialog to prevent discrepancies (store ids?)

 * @apiNote Day of Week Toggle helper class
 * @author  Akash Veerappn
 * @version 1.0
 * @since   2020-7-18

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

    public void manageState (boolean enable) { // improve UI
        sundayButton.setEnabled(enable);
        sundayButton.setFocusable(enable);
        mondayButton.setEnabled(enable);
        mondayButton.setFocusable(enable);
        tuesDayButton.setEnabled(enable);
        tuesDayButton.setFocusable(enable);
        wednesdayButton.setEnabled(enable);
        wednesdayButton.setFocusable(enable);
        thursdayButton.setEnabled(enable);
        thursdayButton.setFocusable(enable);
        fridayButton.setEnabled(enable);
        fridayButton.setFocusable(enable);
        saturdayButton.setEnabled(enable);
        saturdayButton.setFocusable(enable);
    }

    /**

     * @return string of characters for the days chosen
     *
     * @return
     */

    public String getToggleStateString () {
        String checkedStateString = "";
        if (sundayButton.isChecked()) {
            checkedStateString += "Sn";
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
            checkedStateString += "St";
        }
        return checkedStateString;
    }


    public void clearToggles () {
        sundayButton.setChecked(false);
        mondayButton.setChecked(false);
        tuesDayButton.setChecked(false);
        wednesdayButton.setChecked(false);
        thursdayButton.setChecked(false);
        fridayButton.setChecked(false);
        saturdayButton.setChecked(false);
    }

    /**
     * @param selectedDaysString provide the string of selected days obtained from the previous method defined
     * @implNote set the buttons that are present in the string to checked (not selected -> different. Select makes the background colorAccent)
     */


    public void setCheckedToggleButtons(String selectedDaysString) {
        if (selectedDaysString.length() > 0) {
            if (selectedDaysString.contains("Sn")) {
                sundayButton.setChecked(true);
            } else {
                sundayButton.setChecked(false);
            }
            if (selectedDaysString.contains("M")) {
                mondayButton.setChecked(true);
            } else {
                mondayButton.setChecked(false);
            }
            if (selectedDaysString.contains("T")) {
                tuesDayButton.setChecked(true);
            } else {
                tuesDayButton.setChecked(false);
            }
            if (selectedDaysString.contains("W")) {
                wednesdayButton.setChecked(true);
            } else {
                wednesdayButton.setChecked(false);
            }
            if (selectedDaysString.contains("R")) {
                thursdayButton.setChecked(true);
            } else {
                thursdayButton.setChecked(false);
            }
            if (selectedDaysString.contains("F")) {
                fridayButton.setChecked(true);
            } else {
                fridayButton.setChecked(false);
            }
            if (selectedDaysString.contains("St")) {
                saturdayButton.setChecked(true);
            } else {
                saturdayButton.setChecked(false);
            }
        }
    }



}
