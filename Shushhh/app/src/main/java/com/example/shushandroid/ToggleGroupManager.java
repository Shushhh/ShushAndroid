package com.example.shushandroid;

import android.view.View;

import com.google.android.material.button.MaterialButton;

public class ToggleGroupManager {

    private MaterialButton sundayButton;
    private MaterialButton mondayButton;
    private MaterialButton tuesDayButton;
    private MaterialButton wednesdayButton;
    private MaterialButton thursdayButton;
    private MaterialButton fridayButton;
    private MaterialButton saturdayButton;

    public ToggleGroupManager (View view) {
        sundayButton = view.findViewById(R.id.sunday);
        mondayButton = view.findViewById(R.id.monday);
        tuesDayButton = view.findViewById(R.id.tuesday);
        wednesdayButton = view.findViewById(R.id.wednesday);
        thursdayButton = view.findViewById(R.id.thursday);
        fridayButton = view.findViewById(R.id.friday);
        saturdayButton = view.findViewById(R.id.saturday);
    }

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

    public void setCheckedToggleButtons(String selectedDaysString) {
        if (selectedDaysString.charAt(0) == 'S') {
            sundayButton.setChecked(true);
        }
        if (selectedDaysString.contains("M")) {
            mondayButton.setChecked(true);
        }
        if (selectedDaysString.contains("T")) {
            tuesDayButton.setChecked(true);
        }
        if (selectedDaysString.contains("W")) {
            wednesdayButton.setChecked(true);
        }
        if (selectedDaysString.contains("R")) {
            thursdayButton.setChecked(true);
        }
        if (selectedDaysString.contains("F")) {
            fridayButton.setChecked(true);
        }
        if (selectedDaysString.charAt(selectedDaysString.length() - 1) == 'S') {
            saturdayButton.setChecked(true);
        }
    }



}
