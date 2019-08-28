package com.globalm.platform.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class DatePickerUtil {
    public static void setupDatePicker(Context context, ImageView pickerIv, TextView date) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = getDateSetListener(calendar, date);
        pickerIv.setOnClickListener((v) -> {
            DatePickerDialog dialog = showDatePicker(dateSetListener, calendar, context);
            dialog.show();
        });
    }


    private static DatePickerDialog.OnDateSetListener getDateSetListener(
            Calendar calendar,
            TextView dateEd) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateView(calendar, dateEd);
        };
    }

    private static DatePickerDialog showDatePicker(DatePickerDialog.OnDateSetListener date, Calendar calendar, Context context) {
        return new DatePickerDialog(
                context,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static void updateView(Calendar calendar, TextView dateEd) {
        SimpleDateFormat dateFormat = getDateFormat();
        dateEd.setText(dateFormat.format(calendar.getTime()));
    }

    private static SimpleDateFormat getDateFormat() {
        String format = "MM/dd/yyyy";
        return new SimpleDateFormat(format, Locale.US);
    }
}
