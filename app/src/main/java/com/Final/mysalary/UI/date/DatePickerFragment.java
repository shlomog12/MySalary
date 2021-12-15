package com.Final.mysalary.UI.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.Final.mysalary.db.Callback;

import java.time.LocalDateTime;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    private EditText editText;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;
        String monthStr = (month > 9) ? String.valueOf(month) : "0"+month;
        String dayStr = (day > 9) ? String.valueOf(day) : "0"+day;
        this.editText.setText(dayStr+"/"+monthStr+"/"+year);
    }

    public void setEdit(EditText editText){
        this.editText = editText;
    }
}
