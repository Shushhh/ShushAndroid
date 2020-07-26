package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class TimeDialog extends DialogFragment {

    /*
     * View definitions for TimeDialog
     */

    private View view;
    private ImageButton closeButton;
    private Button saveButton;
    private TextView dateTextView1;
    private TextView timeTextView1;
    private TextView timeTextView2;
    private TextInputEditText addNameEditText;

    /*
     * Utility objects
     */

    private ToggleGroupManager toggleGroupManager;
    private ShushObject shushObject;
    private TimePickerFragment timePicker;
    private DatabaseManager databaseManager;

    /*
     * Strings to set the text in the dialog after recycler item click
     */

    private String presetNameString = ""; // string for name
    private String presetDataString = ""; // string for time
    private String presetSupplementalDataString = ""; // string for either the date or the repeatedDates
    private String presetDateTextString = ""; // current date string for when the data string is repeatable days instead of a date
    private String presetTimeString1 = ""; // modified string from presetDataString
    private String presetTimeString2 = ""; // modified string from presetDataString
    private String presetUUIDString = ""; // uuid string

    private String from = ""; // from where: fab or click
    private String currentDate = "", currentTime1 = "", currentTime2 = ""; // the current date and time to be displayed when the user launches TimeDialog with a FAB

    static TimeDialog newInstance() {
        return new TimeDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.time_dialog, container, false);

        closeButton = view.findViewById(R.id.fullscreen_dialog_close);
        saveButton = view.findViewById(R.id.fullscreen_dialog_action);
        dateTextView1 = view.findViewById(R.id.firstdate);
        timeTextView1 = view.findViewById(R.id.firsttime);
        timeTextView2 = view.findViewById(R.id.secondtime);
        addNameEditText = view.findViewById(R.id.addNameEditText);

        toggleGroupManager = new ToggleGroupManager(view);
        shushObject = new ShushObject();
        timePicker = new TimePickerFragment(getActivity());
        databaseManager = new DatabaseManager(getActivity());

        if (this.from.equals("click")) { // if user comes here with recycler item click
            if (!presetNameString.isEmpty()) { // if nameString is not empty (check bundle code in the show method)
                addNameEditText.post(() -> {
                    addNameEditText.setText(presetNameString); // update EditText in a separate UIThread (still not sure why the main thread won't update it properly)
                });
            }
            if (!presetTimeString1.isEmpty() && !presetTimeString2.isEmpty()) { // if time strings aren't empty, update time text views
                timeTextView1.setText(presetTimeString1);
                timeTextView2.setText(presetTimeString2);
            }
            if (!presetDateTextString.isEmpty()) { // if the preset date string is not empty (this item has repeatable days)
                dateTextView1.setText(presetDateTextString); // set the repeatable days string
                toggleGroupManager.setCheckedToggleButtons(presetSupplementalDataString); // check toggle buttons
            } else if (!presetSupplementalDataString.isEmpty()) { // else if it is not repeatable days, it has to be a particular day, so update that
                dateTextView1.setText(presetSupplementalDataString);
            }
        } else if (this.from.equals("fab")) { // if user comes here from fab action, set the current date and time
            if (!currentDate.isEmpty() && !currentTime1.isEmpty() && !currentTime2.isEmpty()) {
                dateTextView1.setText(currentDate);
                timeTextView1.setText(currentTime1);
                timeTextView2.setText(currentTime2);
            }
        }

        closeButton.setOnClickListener(v -> {
            dismiss();
        });

        saveButton.setOnClickListener(v -> {

            String time1 = timeTextView1.getText().toString();
            String time2 = timeTextView2.getText().toString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

            try {
                Date date1 = simpleDateFormat.parse(time1);
                Date date2 = simpleDateFormat.parse(time2);

                if (this.from.equals("fab")) { // if the user comes in from the fab action click
                    if (!addNameEditText.getText().toString().isEmpty()) {
                        if (date2.after(date1)) {
                            if (!toggleGroupManager.getToggleStateString().isEmpty()) {
                                // set all the data to a shushObject (defined above) and insert into Database (not update)
                                shushObject.setName(addNameEditText.getText().toString());
                                shushObject.setData(timeTextView1.getText().toString() + " - " + timeTextView2.getText().toString());
                                shushObject.setSupplementalData(toggleGroupManager.getToggleStateString()); // set the repeatable days string
                                shushObject.setType(ShushObject.ShushObjectType.TIME.getDescription());
                                shushObject.setUUID(UUID.randomUUID().toString());
                                Log.i("Shush", shushObject.toString());
                                if (databaseManager.insert(shushObject)) {
                                    TimeTab.updateRecyclerView();
                                    dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Problem saving data. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            } else { // if there are no repeatable days, then just push the date and not the repeatable days as done in the previous block
                                shushObject.setName(addNameEditText.getText().toString());
                                shushObject.setData(timeTextView1.getText().toString() + " - " + timeTextView2.getText().toString());
                                shushObject.setSupplementalData(dateTextView1.getText().toString());
                                shushObject.setType(ShushObject.ShushObjectType.TIME.getDescription());
                                shushObject.setUUID(UUID.randomUUID().toString());
                                Log.i("Shush", shushObject.toString());
                                if (databaseManager.insert(shushObject)) {
                                    TimeTab.updateRecyclerView();
                                    dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Problem saving data. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please ensure the second time is after the first time.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please enter a name for your time constraint. Ex: Work/Study.", Toast.LENGTH_LONG).show();
                    }
                } else if (this.from.equals("click")) { // if the user comes in from a recycler item click, do the same thing as above but update the database instead of inserting data
                    if (!presetUUIDString.isEmpty()) {
                        if (!addNameEditText.getText().toString().isEmpty()) {
                            if (date2.after(date1)) {
                                if (!toggleGroupManager.getToggleStateString().isEmpty()) {
                                    shushObject.setName(addNameEditText.getText().toString());
                                    shushObject.setData(timeTextView1.getText().toString() + " - " + timeTextView2.getText().toString());
                                    shushObject.setSupplementalData(toggleGroupManager.getToggleStateString());
                                    shushObject.setType(ShushObject.ShushObjectType.TIME.getDescription());
                                    shushObject.setUUID(presetUUIDString);
                                    if (databaseManager.update(shushObject)) {
                                        TimeTab.updateRecyclerView();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Problem saving data. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    shushObject.setName(addNameEditText.getText().toString());
                                    shushObject.setData(timeTextView1.getText().toString() + " - " + timeTextView2.getText().toString());
                                    shushObject.setSupplementalData(dateTextView1.getText().toString());
                                    shushObject.setType(ShushObject.ShushObjectType.TIME.getDescription());
                                    shushObject.setUUID(presetUUIDString);
                                    Log.i("Shush", shushObject.toString());
                                    if (databaseManager.update(shushObject)) {
                                        TimeTab.updateRecyclerView();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Problem saving data. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please ensure the second time is after the first time.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please enter a name for your time constraint. Ex: Work/Study.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        /*
         * provide onClick for all the date and time textviews and pass the respective views to the date and time pickers to preset the time and date
         */

        dateTextView1.setOnClickListener(v -> {
            DatePickerFragment datePicker1 = new DatePickerFragment(getActivity());
            datePicker1.setTextView(dateTextView1);
            datePicker1.show(getFragmentManager(), "date picker 1");
        });

        timeTextView1.setOnClickListener(v -> {
            timePicker.setTextView(timeTextView1);
            timePicker.show(getFragmentManager(), "time picker 1");
        });

        timeTextView2.setOnClickListener(v -> {
            timePicker.setTextView(timeTextView2);
            timePicker.show(getFragmentManager(), "time picker 1");
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show(FragmentManager fragmentManager, @Nullable String tag, String from) {
        presetDateTextString = "";
        this.from = from;
        if (from.equals("click")) {
            if (getArguments() != null) {

                // receive the bundle from the recycler adapter class's onClick

                presetNameString = getArguments().getString(DatabaseManager.DatabaseEntry.NAME);
                presetDataString = getArguments().getString(DatabaseManager.DatabaseEntry.DATA);
                presetSupplementalDataString = getArguments().getString(DatabaseManager.DatabaseEntry.SUPP);
                presetUUIDString = getArguments().getString(DatabaseManager.DatabaseEntry.UUID);

                if (!presetSupplementalDataString.isEmpty() && !presetSupplementalDataString.contains(",")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy");
                    presetDateTextString = format.format(calendar.getTime());
                }

                // gather separate time strings from the data string obtained from the bundle

                int index = 0;
                for (Character character: presetDataString.toCharArray()) {
                    if (character == '-') {
                        presetTimeString1 = presetDataString.substring(0, index - 1);
                        presetTimeString2 = presetDataString.substring(index + 2);
                    }
                    index = index + 1;
                }
            }
        } else if (from.equals("fab")) { // if the user comes from the fab, then set the current date and time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
            currentDate = simpleDateFormat.format(new Date());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.HOUR_OF_DAY, 1); // set toTime to 1 hour after fromTime
            currentTime1 = timeFormat.format(new Date());
            currentTime2 = timeFormat.format(c.getTime());
        }
        super.show(fragmentManager, tag);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Context context;
        private TextView textView;

        DatePickerFragment (Context context) {
            this.context = context;
        }


        /**
         * @implNote set the current date to the calendar datepicker for ease of access and presentability
         */

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, year, month, day);

            if (textView != null) {
                String text = textView.getText().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
                try {
                    Date date = simpleDateFormat.parse(text);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

        /**
         * @implNote set the date to the textView when the date has been set
         */

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);

            if (textView != null) {
                SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy");
                textView.setText(format.format(c.getTime()));
            }
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private Context context;
        private TextView textView;

        TimePickerFragment(Context context) {
            this.context = context;
        }

        /**
         * @implNote set current time from the textView
         */

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, this, hour, minute, false);
            String text = textView.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            try {
                Date date = simpleDateFormat.parse(text);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                timePickerDialog.updateTime(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return timePickerDialog;
        }

        /**
         * manage AM PM conversions and set the appropriate time to the textView
         */

        @SuppressLint("SetTextI18n")
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
            String amOrPm = "";
            String hour = "", minute = "";

            if (hourOfDay < 12) {
                amOrPm = "AM";
                if (hourOfDay == 0) {
                    hour = "12";
                } else {
                    if (hourOfDay < 10) {
                        hour = "0" + hourOfDay;
                    } else {
                        hour = "" + hourOfDay;
                    }
                }
            } else if (hourOfDay <= 23) {
                amOrPm = "PM";
                if (hourOfDay == 12) {
                    hour = "" + hourOfDay;
                } else {
                    if (hourOfDay < 22) {
                        hour = "0" + (hourOfDay - 12);
                    } else {
                        hour = "" + (hourOfDay - 12);
                    }
                }
            }

            if (minuteOfDay < 10) {
                minute = "0" + minuteOfDay;
            } else {
                minute = "" + minuteOfDay;
            }

            if (textView != null)
                textView.setText(hour + ":" + minute + " " + amOrPm);

        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

    }

}