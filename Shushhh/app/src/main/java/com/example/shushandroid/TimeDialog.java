package com.example.shushandroid;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
        TextView textView = view.findViewById(R.id.firstdate);

        close.setOnClickListener(v -> {
            dismiss();
        });
        action.setOnClickListener(v -> {
            dismiss();
        });
        textView.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment(getActivity());
            datePicker.show(getFragmentManager(), "date picker");
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

}