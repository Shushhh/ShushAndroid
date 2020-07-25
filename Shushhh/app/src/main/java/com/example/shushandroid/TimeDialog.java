package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

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

        if (!presetNameString.isEmpty()) {
            Log.i("Text", presetNameString);
            Log.i("EditText", addNameEditText.getText().toString());
            addNameEditText.post(new Runnable() {
                @Override
                public void run() {
                    addNameEditText.setText(presetNameString);
                }
            });
            Log.i("Text2", presetNameString);
        }

        if (!presetSupplementalDataString.isEmpty()) {
            dateTextView1.setText(presetSupplementalDataString);
        }

        closeButton.setOnClickListener(v -> {
            dismiss();
        });

        saveButton.setOnClickListener(v -> {
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
        if (from.equals("click")) {
            if (getArguments() != null) {
                presetNameString = getArguments().getString(DatabaseManager.DatabaseEntry.NAME);
                presetDataString = getArguments().getString(DatabaseManager.DatabaseEntry.DATA);
                presetSupplementalDataString = getArguments().getString(DatabaseManager.DatabaseEntry.SUPP);
                // 2 formats: supp is either togglestring or date

                Log.i("Name", presetNameString);

                if (presetSupplementalDataString != null) {
                    if (presetSupplementalDataString.contains(",")) {
                        try {
                            Log.i("Sequence", "Parse");
                            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy");
                            Date d = format.parse(presetSupplementalDataString);
                            LocalDate localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            Calendar c = Calendar.getInstance();
                            c.set(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        } else if (from.equals("fab")) {
            Log.i("Dialog", "fab");
        }
        super.show(fragmentManager, tag);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Context context;
        private TextView textView;
        public static Calendar selectedCalendar;

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
            if (selectedCalendar != null) {
                datePickerDialog.updateDate(selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DATE));
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

        private Date date;
        private int hour, minute;

        TimePickerFragment(Context context) {
            this.context = context;
        }

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(context, this, hour, minute, false);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
            String amOrPm = "";
            String hour = "", minute = "";

            this.hour = hourOfDay;
            this.minute = minuteOfDay;

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