package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TimeDialog extends DialogFragment {

    private View view;
    private ImageButton closeButton;
    private Button saveButton;
    private TextView dateTextView1;
    private TextView timeTextView1;
    private TextView timeTextView2;
    private TextInputEditText addNameEditText;

    private ToggleGroupManager toggleGroupManager;
    private ShushObject shushObject;
    private TimePickerFragment timePicker;

    private DatabaseManager databaseManager;

    private String presetNameString = "";
    private String presetDataString = "";
    private String presetSupplementalDataString = "";
    private String presetDateTextString = "";
    private String presetTimeString1 = "";
    private String presetTimeString2 = "";

    private String from = "";
    private String currentDate = "", currentTime1 = "", currentTime2 = "";

    static TimeDialog newInstance() {
        return new TimeDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

        Log.i("Sequence", "Create");
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

        if (this.from.equals("click")) {

            if (!presetNameString.isEmpty()) {
                Log.i("Text1", presetNameString);
                addNameEditText.post(() -> {
                    addNameEditText.setText(presetNameString);
                });
                Log.i("Text2", presetNameString);
            }

            if (!presetTimeString1.isEmpty() && !presetTimeString2.isEmpty()) {
                timeTextView1.setText(presetTimeString1);
                timeTextView2.setText(presetTimeString2);
            }

            if (!presetDateTextString.isEmpty()) {
                dateTextView1.setText(presetDateTextString);
            } else if (!presetSupplementalDataString.isEmpty()) {
                dateTextView1.setText(presetSupplementalDataString);
            }
        } else if (this.from.equals("fab")) {
            dateTextView1.setText(currentDate);
            timeTextView1.setText(currentTime1);
            timeTextView2.setText(currentTime2);
        }

        closeButton.setOnClickListener(v -> {
            dismiss();
        });

        saveButton.setOnClickListener(v -> {
            if (this.from.equals("fab")) {
                if (!addNameEditText.getText().toString().isEmpty()) {
                    if (!toggleGroupManager.getToggleStateString().isEmpty()) {
                        shushObject.setName(addNameEditText.getText().toString());
                        shushObject.setData(timeTextView1.getText().toString() + " - " + timeTextView2.getText().toString());
                        shushObject.setSupplementalData(toggleGroupManager.getToggleStateString());
                        shushObject.setType(ShushObject.ShushObjectType.TIME.getDescription());
                        Log.i("Shush", shushObject.toString());
                        if (databaseManager.insert(shushObject)) {
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
                        Log.i("Shush", shushObject.toString());
                        if (databaseManager.insert(shushObject)) {
                            TimeTab.updateRecyclerView();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Problem saving data. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter a name for your time constraint. Ex: Work/Study.", Toast.LENGTH_LONG).show();
                }
            } else if (this.from.equals("fab")) {

            }
        });

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

                presetNameString = getArguments().getString(DatabaseManager.DatabaseEntry.NAME);
                presetDataString = getArguments().getString(DatabaseManager.DatabaseEntry.DATA);
                presetSupplementalDataString = getArguments().getString(DatabaseManager.DatabaseEntry.SUPP);

                if (!presetSupplementalDataString.isEmpty() && !presetSupplementalDataString.contains(",")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy");
                    presetDateTextString = format.format(calendar.getTime());
                }

                int index = 0;
                for (Character character: presetDataString.toCharArray()) {
                    if (character == '-') {
                        presetTimeString1 = presetDataString.substring(0, index - 1);
                        presetTimeString2 = presetDataString.substring(index + 2);
                        Log.i("Time", presetTimeString1 + "-" + presetTimeString2);
                    }
                    index = index + 1;
                }
            }
        } else if (from.equals("fab")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
            currentDate = simpleDateFormat.format(new Date());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.HOUR_OF_DAY, 1);
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

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        private Context context;
        private TextView textView;

        TimePickerFragment(Context context) {
            this.context = context;
        }

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