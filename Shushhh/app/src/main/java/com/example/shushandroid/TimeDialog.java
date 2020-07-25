package com.example.shushandroid;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TimeDialog extends DialogFragment {

    private View view;
    private ImageButton closeButton;
    private Button saveButton;
    private TextView dateTextView1;
    private TextView dateTextView2;
    private TextView timeTextView1;
    private TextView timeTextView2;
    private EditText addNameEditText;

    private ToggleGroupManager toggleGroupManager;
    private ShushObject shushObject;
    private TimePickerFragment timePicker;

    private DatabaseManager databaseManager;

    private String presetNameString;
    private String presetDataString;
    private String presetSupplementalDataString;

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
        dateTextView2 = view.findViewById(R.id.seconddate);
        timeTextView1 = view.findViewById(R.id.firsttime);
        timeTextView2 = view.findViewById(R.id.secondtime);
        addNameEditText = view.findViewById(R.id.addNameEditText);

        toggleGroupManager = new ToggleGroupManager(view);
        shushObject = new ShushObject();
        timePicker = new TimePickerFragment(getActivity());

        databaseManager = new DatabaseManager(getActivity());

        dateTextView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dateTextView2.setText(dateTextView1.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                    shushObject.setSupplementalData(dateTextView1.getText().toString() + " " + timeTextView1.getText().toString() + "-" + timeTextView2.getText().toString());
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

        dateTextView2.setOnClickListener(v -> {
            DatePickerFragment datePicker2 = new DatePickerFragment(getActivity());
            datePicker2.setTextView(dateTextView2);
            datePicker2.setConstraintTextView(dateTextView1);
            datePicker2.show(getFragmentManager(), "date picker 2");
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

    public void show(FragmentManager fragmentManager, @Nullable String tag, String from) {
        if (from.equals("click")) {
            if (getArguments() != null) {
                presetNameString = getArguments().getString(DatabaseManager.DatabaseEntry.NAME);
                presetDataString = getArguments().getString(DatabaseManager.DatabaseEntry.DATA);
                presetSupplementalDataString = getArguments().getString(DatabaseManager.DatabaseEntry.SUPP);
                Log.i("Arguments", presetNameString + " | " + presetDataString + " | " + presetSupplementalDataString);
            }
        } else if (from.equals("fab")) {
            Log.i("Dialog", "fab");
        }
        super.show(fragmentManager, tag);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Context context;
        private TextView textView;
        private TextView constraintTextView;

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
            if (constraintTextView == null) {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            } else {
                try {
                    Date date = new SimpleDateFormat("EEE, MMM dd, yyyy").parse(constraintTextView.getText().toString());
                    datePickerDialog.getDatePicker().setMinDate(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy");
            if (textView != null) {
                textView.setText(format.format(c.getTime()));
            }
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public void setConstraintTextView(TextView constraintTextView) {
            this.constraintTextView = constraintTextView;
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
            return new TimePickerDialog(context, this, hour, minute, false);
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