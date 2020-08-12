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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private Button deleteButton;

    private ToggleGroupManager toggleGroupManager;
    private TimePickerFragment timePicker;
    private DatabaseManager databaseManager;

    private String presetUUIDString;

    private boolean isFromFab = true;

    public ShushObject shushObject;
    LatLng latlng;

    static ShushDialog newInstance() {
        return new ShushDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String location = data.getStringExtra(LocationDataTransferItem.LOCATION);
                    String radius = data.getStringExtra(LocationDataTransferItem.RADIUS);
                    Bundle bundle = data.getParcelableExtra("bundle");
                    latlng = bundle.getParcelable("latlng");
                    Log.i("Activity Result", location + " " + radius);
                    if (mapTextView != null) {
                        if (location == null) {
                            mapTextView.setText(ShushObject.Key.NULL);
                        } else {
                            mapTextView.setText(location);
                            toggleGroupManager.clearToggles();
                            toggleGroupManager.manageState(false);
                        }
                    }
                    if (radiusTextView != null) {
                        if (radius == null) {
                            radiusTextView.setText(ShushObject.Key.NULL);
                        } else {
                            radiusTextView.setText(radius);
                            toggleGroupManager.clearToggles();
                            toggleGroupManager.manageState(false);
                        }
                    }
                } else {
                    mapTextView.setText(ShushObject.Key.NULL);
                    radiusTextView.setText(ShushObject.Key.NULL);
                    toggleGroupManager.manageState(true);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        deleteButton = view.findViewById(R.id.deleteButton);
        mapTextView = view.findViewById(R.id.locationTextView);
        radiusTextView = view.findViewById(R.id.radiusTextView);

        toggleGroupManager = new ToggleGroupManager(view);
        shushObject = new ShushObject();
        timePicker = new TimePickerFragment(getActivity());
        databaseManager = new DatabaseManager(getActivity());

        Log.i("View", "View");

        if (getArguments() != null) {
            presetUUIDString = getArguments().getString(DatabaseManager.DatabaseEntry.UUID);
            Log.i("UUID", presetUUIDString);
                ShushObject shushObject = databaseManager.getShushObject(presetUUIDString);

            if (shushObject != null) {
                Log.i("Click", "Click");

                addNameEditText.post(() -> {
                    addNameEditText.setText(shushObject.getName());
                    toggleGroupManager.setCheckedToggleButtons(shushObject.getRep());

                });

                dateTextView1.setText(shushObject.getDate());
                mapTextView.setText(shushObject.getLocation());
                radiusTextView.setText(shushObject.getRadius());

                System.out.println("REP: " + toggleGroupManager.getToggleStateString() + " " + shushObject.getRep());

                timeTextView1.setText(shushObject.getTime().substring(0, shushObject.getTime().indexOf("-") - 1).trim());
                timeTextView2.setText(shushObject.getTime().substring(shushObject.getTime().indexOf("-") + 1).trim());

                System.out.println(timeTextView2.getText());

                if (!shushObject.getLocation().equals(ShushObject.Key.NULL)) {
                    toggleGroupManager.manageState(false);
                }
            }
            isFromFab = false;
        } else {
            isFromFab = true;
        }

        timeClearButton.setOnClickListener(v -> {
            timeTextView1.setText(ShushObject.Key.NULL);
            timeTextView2.setText(ShushObject.Key.NULL);
            dateTextView1.setText(ShushObject.Key.NULL);
        });

        locationClearButton.setOnClickListener(v -> {
            mapTextView.setText(ShushObject.Key.NULL);
            radiusTextView.setText(ShushObject.Key.NULL);
            toggleGroupManager.manageState(true);
        });

        deleteButton.setOnClickListener(v -> {
            if (presetUUIDString != null && !presetUUIDString.isEmpty()) {
                if (!databaseManager.delete(presetUUIDString)) {
                    Log.e("DB Error", "Error deleting " + presetUUIDString + " ShushObject");
                } else {
                    MainActivity.updateRecyclerView();
                    ArrayList<ShushObject> shushObjects = databaseManager.retrieveWithCursor();
                    try {
                        ShushQueryScheduler.schedule(shushObjects, getContext());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
            }
        });

        mapTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra("Location", mapTextView.getText().toString());
            startActivityForResult(intent, 10);
        });

        radiusTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra("Location", mapTextView.getText().toString());
            startActivityForResult(intent, 10);
        });

        closeButton.setOnClickListener(v -> {
            if (presetUUIDString != null) {
                toggleGroupManager.uncheckToggleGroup(toggleGroupManager.getToggleStateString(), databaseManager.getShushObject(presetUUIDString).getRep());
                dismiss();
            }
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
                    if (date.equals(ShushObject.Key.NULL) && time1.equals(ShushObject.Key.NULL) && time2.equals(ShushObject.Key.NULL) && location.equals(ShushObject.Key.NULL) && radius.equals(ShushObject.Key.NULL)) {
                        new AlertDialog.Builder(getActivity()).setTitle("Location and time fields left blank")
                                .setMessage("Please fill either or both location and time fields to provide a constraint for silencing your phone.")
                                .setPositiveButton("Ok", null)
                                .create().show();
                        goLocation = false;
                        goTime = false;
                    } else {
                        if (!date.equals(ShushObject.Key.NULL) && !time1.equals(ShushObject.Key.NULL) && !time2.equals(ShushObject.Key.NULL)) {
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
                            if (!(date.equals(ShushObject.Key.NULL) && time1.equals(ShushObject.Key.NULL) && time2.equals(ShushObject.Key.NULL))) {
                                if (date.equals(ShushObject.Key.NULL)) {
                                    new AlertDialog.Builder(getActivity()).setTitle("Date field left blank")
                                            .setMessage("Please enter a date for your constraint. Click on the date field to access a date picker.")
                                            .setPositiveButton("Ok", null)
                                            .create().show();
                                    goTime = false;
                                } else if (time1.equals(ShushObject.Key.NULL) || time2.equals(ShushObject.Key.NULL)) {
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
                        if (!location.equals(ShushObject.Key.NULL) && !radius.equals(ShushObject.Key.NULL)) {
                            shushObject.setLocation(location);
                            shushObject.setRadius(radius);
                            goLocation = true;
                        } else if (!(location.equals(ShushObject.Key.NULL) && radius.equals(ShushObject.Key.NULL))) {
                            if (location.equals(ShushObject.Key.NULL)) {
                                new AlertDialog.Builder(getActivity()).setTitle("Location field left blank")
                                        .setMessage("Please enter a location for your constraint. Click on the location field to access the map.")
                                        .setPositiveButton("Ok", null)
                                        .create().show();
                                goLocation = false;
                            } else {
                                if (radius.equals(ShushObject.Key.NULL)) {
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
                shushObject.setLatLng(latlng);

                Log.i("Latlng", latlng.toString());

                if (isFromFab) {
                    Log.i("Info", "FAB");
                    shushObject.setUUID(UUID.randomUUID().toString());
                    if (databaseManager.insert(shushObject)) {
                        MainActivity.updateRecyclerView();
                        ArrayList<ShushObject> shushObjects = databaseManager.retrieveWithCursor();
                        try {
                            ShushQueryScheduler.schedule(shushObjects, getContext());
                            GeofenceManager.addGeofences(shushObjects, getContext());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dismiss();
                    }
                } else {
                    Log.i("Info", "Click");
                    shushObject.setUUID(presetUUIDString);
                    if (databaseManager.update(shushObject)) {
                        MainActivity.updateRecyclerView();
                        ArrayList<ShushObject> shushObjects = databaseManager.retrieveWithCursor();
                        try {
                            ShushQueryScheduler.schedule(shushObjects, getContext());
                            GeofenceManager.addGeofences(shushObjects, getContext());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
                if (!text.equals(ShushObject.Key.NULL)) {
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
            if (!text.equals(ShushObject.Key.NULL)) {
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