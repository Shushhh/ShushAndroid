package com.example.shushandroid;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.Image;
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

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class TimeDialog extends DialogFragment {

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
        View view = inflater.inflate(R.layout.time_dialog, container, false);
        ImageButton close = view.findViewById(R.id.fullscreen_dialog_close);
        Button action = view.findViewById(R.id.fullscreen_dialog_action);
        TextView date1 = view.findViewById(R.id.firstdate);
        TextView date2 = view.findViewById(R.id.seconddate);
        TextView time1 = view.findViewById(R.id.firsttime);
        Button sunday = view.findViewById(R.id.sunday);

        close.setOnClickListener(v -> {
            dismiss();
        });
        action.setOnClickListener(v -> {
            dismiss();
        });
        date1.setOnClickListener(v -> {
            DialogFragment datePicker1 = new DatePickerFragment(getActivity());
            datePicker1.show(getFragmentManager(), "date picker 1");
        });
        date2.setOnClickListener(v -> {
            DialogFragment datePicker2 = new DatePickerFragment(getActivity());
            datePicker2.show(getFragmentManager(), "date picker 2");
        });

        //Onclick crashes app because of TimePickerFragment
        time1.setOnClickListener(v -> {
            DialogFragment timePicker1 = new TimePickerFragment(getActivity());
            timePicker1.show(getFragmentManager(), "time picker 1");
        });

        //Not working -> created new xml file that changes color based off of state
        sunday.setOnClickListener(v -> {
            sunday.isSelected();
        });

        return view;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Context context;

        DatePickerFragment (Context context) {
            this.context = context;
        }

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(context, this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            Log.i("Date", currentDateString);
        }
    }

    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        private Context context;

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
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            String currentTimeString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            Log.i("Time", currentTimeString);

        }
    }

}