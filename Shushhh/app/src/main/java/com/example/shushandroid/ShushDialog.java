package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

/**
 * @apiNote Time Dialog class
 * @author  Akash Veerappan and Sahil Sudhir
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class ShushDialog extends DialogFragment {

    public static class LocationDataTransferItem {
        public static String LOCATION = "LOCATION";
        public static String RADIUS = "RADIUS";
    }

    public static class DateFormatStringKey {
        public static String dateFormatString = "EEE, MMM dd, yyyy";
        public static String timeFormatString = "hh:mm a";
    }

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
    private TextView mapTextView;
    private TextView radiusTextView;
    private Button timeClearButton;
    private Button locationClearButton;

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
    private String presetTimeString = ""; // string for time
    private String presetDateString = ""; // string for either the date or the repeatedDates
    private String presetRepString = ""; // current date string for when the data string is repeatable days instead of a date
    private String presetTimeString1 = ""; // modified string from presetDataString
    private String presetTimeString2 = ""; // modified string from presetDataString
    private String presetUUIDString = ""; // uuid string
    private String presetLocationString = ""; //location string
    private String presetRadiusString = ""; //radius string

    private String from = ""; // from where: fab or click

    static ShushDialog newInstance() {
        return new ShushDialog();
    }

    /**
     *
     * @param savedInstanceState
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String location = data.getStringExtra(LocationDataTransferItem.LOCATION);
                    String radius = data.getStringExtra(LocationDataTransferItem.RADIUS);
                    Log.i("Activity Result", location + " " + radius);
                    if (mapTextView != null) {
                        if (location == null) {
                            mapTextView.setText("N/A");
                        } else {
                            mapTextView.setText(location);
                        }
                    }
                    if (radiusTextView != null) {
                        if (radius == null) {
                            radiusTextView.setText("N/A");
                        } else {
                            radiusTextView.setText(radius);
                        }
                    }
                } else {
                    mapTextView.setText("N/A");
                    radiusTextView.setText("N/A");
                }
            }
        }
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
        timeClearButton = view.findViewById(R.id.timeClearButton);
        locationClearButton = view.findViewById(R.id.locationClearButton);

        timeClearButton.setOnClickListener(v -> {
            timeTextView1.setText("N/A");
            timeTextView2.setText("N/A");
            dateTextView1.setText("N/A");
        });

        locationClearButton.setOnClickListener(v -> {
            mapTextView.setText("N/A");
            radiusTextView.setText("N/A");
        });

        mapTextView = view.findViewById(R.id.locationTextView);
        radiusTextView = view.findViewById(R.id.radiusTextView);
        mapTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra("Location", mapTextView.getText().toString());
            startActivityForResult(intent, 10);
        });

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
            } else {
                timeTextView1.setText("N/A");
                timeTextView2.setText("N/A");
            }

            toggleGroupManager.setCheckedToggleButtons(presetRepString);
            dateTextView1.setText((!presetDateString.isEmpty() ? presetDateString : "N/A"));
//            if (!presetDateTextString.isEmpty()) { // if the preset date string is not empty (this item has repeatable days)
//                dateTextView1.setText(presetDateTextString); // set the repeatable days string
//                toggleGroupManager.setCheckedToggleButtons(presetSupplementalDataString); // check toggle buttons
//            } else if (!presetSupplementalDataString.isEmpty()) { // else if it is not repeatable days, it has to be a particular day, so update that
//                dateTextView1.setText(presetSupplementalDataString);
//            }

            mapTextView.setText((!presetLocationString.isEmpty() ? presetLocationString : "N/A"));
            radiusTextView.setText((!presetRadiusString.isEmpty() ? presetRadiusString : "N/A"));

        } else if (this.from.equals("fab")) { // if user comes here from fab action, set the current date and time
            dateTextView1.setText("N/A");
            timeTextView1.setText("N/A");
            timeTextView2.setText("N/A");
            mapTextView.setText("N/A");
            radiusTextView.setText("N/A");
        }

        closeButton.setOnClickListener(v -> {
            dismiss();
        });

        saveButton.setOnClickListener(v -> {

            String time1 = timeTextView1.getText().toString();
            String time2 = timeTextView2.getText().toString();
            String name = addNameEditText.getText().toString();
            String date = dateTextView1.getText().toString();
            String location = mapTextView.getText().toString();
            String radius = radiusTextView.getText().toString();

            ShushObject shushObject = new ShushObject();

            Date date1 = null;
            Date date2 = null;

            Boolean goTime = false;
            Boolean goLocation = false;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

            try {
                if (!name.isEmpty()) {
                    if (date.equals("N/A") && time1.equals("N/A") && time2.equals("N/A") && location.equals("N/A") && radius.equals("N/A")) {
                        new AlertDialog.Builder(getActivity()).setTitle("Location and time fields left blank")
                                .setMessage("Please fill either or both location and time fields to provide a constraint for silencing your phone.")
                                .setPositiveButton("Ok", null)
                                .create().show();
                        goLocation = false;
                        goTime = false;
                    } else {
                        if (!date.equals("N/A") && !time1.equals("N/A") && !time2.equals("N/A")) {
                            date1 = simpleDateFormat.parse(time1);
                            date2 = simpleDateFormat.parse(time2);
                            if (date2 != null && !date2.after(date1)) {
                                new AlertDialog.Builder(getActivity()).setTitle("From time is after to time")
                                        .setMessage("Please enter a valid time-frame for your constraint. Ensure your to-time is after your from-time.")
                                        .setPositiveButton("Ok", null)
                                        .create().show();
                                goTime = false;
                            } else {
                                shushObject.setName(name);
                                shushObject.setTime(time1 + " - " + time2);
                                shushObject.setDate(date);
                                goTime = true;
                            }
                        } else {
                            goTime = false;
                            if (!(date.equals("N/A") && time1.equals("N/A") && time2.equals("N/A"))) {
                                if (date.equals("N/A")) {
                                    new AlertDialog.Builder(getActivity()).setTitle("Date field left blank")
                                            .setMessage("Please enter a date for your constraint. Click on the date field to access a date picker.")
                                            .setPositiveButton("Ok", null)
                                            .create().show();
                                    goTime = false;
                                } else if (time1.equals("N/A") || time2.equals("N/A")) {
                                    new AlertDialog.Builder(getActivity()).setTitle("Time field left blank")
                                            .setMessage("Please enter a time for your constraint. Click on the time field to access the time picker.")
                                            .setPositiveButton("Ok", null)
                                            .create().show();
                                    goTime = false;
                                }
                            } else {
                                goTime = true;
                            }
                        }
                        if (!location.equals("N/A") && !radius.equals("N/A")) {
                            shushObject.setLocation(location);
                            shushObject.setRadius(radius);
                            goLocation = true;
                        } else if (!(location.equals("N/A") && radius.equals("N/A"))) {
                            if (location.equals("N/A")) {
                                new AlertDialog.Builder(getActivity()).setTitle("Location field left blank")
                                        .setMessage("Please enter a location for your constraint. Click on the location field to access the map.")
                                        .setPositiveButton("Ok", null)
                                        .create().show();
                                goLocation = false;
                            } else {
                                if (radius.equals("N/A")) {
                                    new AlertDialog.Builder(getActivity()).setTitle("Radius field left blank")
                                            .setMessage("Please enter a radius for your constraint. Click on the location field to access the radius options.")
                                            .setPositiveButton("Ok", null)
                                            .create().show();
                                    goLocation = false;
                                }
                            }
                        } else {
                            goLocation = true;
                        }
                    }
                } else {
                    new AlertDialog.Builder(getActivity()).setTitle("Name field left blank")
                            .setMessage("Please enter a name for your constraint. For example: Work/Study, etc.")
                            .setPositiveButton("Ok", null)
                            .create().show();
                    goLocation = false;
                    goTime = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (goTime && goLocation) {
                shushObject.setName(name);
                shushObject.setTime(time1 + " - " + time2);
                shushObject.setDate(date);
                shushObject.setRep(toggleGroupManager.getToggleStateString());
                shushObject.setLocation(location);
                shushObject.setRadius(radius);

                if (this.from.equals("fab")) {
                    shushObject.setUUID(UUID.randomUUID().toString());
                    if (databaseManager.insert(shushObject)) {
                        MainActivity.updateRecyclerView();
                        dismiss();
                    }
                } else if (this.from.equals("click")) {
                    shushObject.setUUID(presetUUIDString);
                    if (databaseManager.update(shushObject)) {
                        MainActivity.updateRecyclerView();
                        dismiss();
                    }
                }
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

        /**
         *
         */
        timeTextView2.setOnClickListener(v -> {
            timePicker.setTextView(timeTextView2);
            timePicker.show(getFragmentManager(), "time picker 1");
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    public void show(FragmentManager fragmentManager, @Nullable String tag, String from) {
        this.from = from;
        if (from.equals("click")) {
            if (getArguments() != null) {

                presetNameString = (getArguments().getString(DatabaseManager.DatabaseEntry.NAME) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.NAME));
                presetTimeString = (getArguments().getString(DatabaseManager.DatabaseEntry.TIME) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.TIME));
                presetDateString = (getArguments().getString(DatabaseManager.DatabaseEntry.DATE) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.DATE));
                presetRepString = (getArguments().getString(DatabaseManager.DatabaseEntry.REP) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.REP));
                presetUUIDString = (getArguments().getString(DatabaseManager.DatabaseEntry.UUID) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.UUID));
                presetLocationString = (getArguments().getString(DatabaseManager.DatabaseEntry.LOC) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.LOC));
                presetRadiusString = (getArguments().getString(DatabaseManager.DatabaseEntry.RAD) == null ? "" : getArguments().getString(DatabaseManager.DatabaseEntry.RAD));

                int index = 0;
                if (!presetTimeString.isEmpty()) {
                    for (Character character : presetTimeString.toCharArray()) {
                        if (character == '-') {
                            presetTimeString1 = presetTimeString.substring(0, index - 1);
                            presetTimeString2 = presetTimeString.substring(index + 2);
                        }
                        index = index + 1;
                    }
                }
            }
        }

        super.show(fragmentManager, tag);
    }

    /**
     *
     */
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
                if (!text.equals("N/A")) {
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

        /**
         *
         * @param textView
         */
        public void setTextView(TextView textView) {
            this.textView = textView;
        }

    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


        private Context context;
        private TextView textView;

        /**
         *
         * @param context
         */
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
            if (!text.equals("N/A")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                try {
                    Date date = simpleDateFormat.parse(text);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    timePickerDialog.updateTime(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        /**
         *
         * @param textView
         */
        public void setTextView(TextView textView) {
            this.textView = textView;
        }

    }

}