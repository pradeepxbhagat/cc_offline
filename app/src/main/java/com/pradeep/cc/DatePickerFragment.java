package com.pradeep.cc;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import com.pradeep.cc.util.Date;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    static final String TITLE_KEY = "TITLE_KEY";
    static final String COMPARISION_DATE = "COMPARISION_DATE";
    static final String COMPARISION_TYPE = "COMPARISION_TYPE";
    static final int LESS_THEN = 1401;
    static final int GREATER_THEN = 1402;
    private boolean shouldCloseDialog = true;

    private Date date;
    private DateListener dateListener;

    static DatePickerFragment getInstance(Bundle args) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dateListener = (DateListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        date = new Date(day, month, year);

        final String title = getArguments().getString(TITLE_KEY, "Select date");
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day) {
            @Override
            public void onBackPressed() {
                shouldCloseDialog = true;
                super.onBackPressed();
            }

            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    shouldCloseDialog = true;
                    dateListener.onDialogCancel();
                }
                super.onClick(dialog, which);
            }

            @Override
            public void dismiss() {
                if(shouldCloseDialog) {
                    super.dismiss();
                }
            }
        };
        datePickerDialog.setTitle(title);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date.setDay(dayOfMonth);
        date.setMonth(month);
        date.setYear(year);

        Date comparisonDate = getArguments().getParcelable(COMPARISION_DATE);
        if (comparisonDate != null) {
            int operator = getArguments().getInt(COMPARISION_TYPE, LESS_THEN);
            switch (operator) {
                case LESS_THEN:
                    if (comparisonDate.getTimeStamp() > date.getTimeStamp()) {
                        Toast.makeText(getContext(), "End date cannot be less then start date", Toast.LENGTH_LONG).show();
                        shouldCloseDialog = false;
                        return;
                    }
                    break;
                default:
                    break;
            }

        }

        if (dateListener != null) {
            dateListener.onDateSelected(date);
        }
    }

    interface DateListener {
        void onDateSelected(Date date);
        void onDialogCancel();
    }
}
