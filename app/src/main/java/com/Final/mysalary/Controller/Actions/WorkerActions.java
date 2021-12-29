package com.Final.mysalary.Controller.Actions;

import android.app.Dialog;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.*;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.Job;
import com.Final.mysalary.Model.DTO.Shift;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkerActions extends UiActions{

    @RequiresApi(api = Build.VERSION_CODES.O)
    public WorkerActions(AppCompatActivity activity) {
        super(activity);
    }

    public void addNewShift() {
        DB.getJobs(currentUser.getMail(), new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobs) {
                addNewShiftToJobFromJobs(jobs);
            }
        });
    }

    private void addNewShiftToJobFromJobs(ArrayList<String> jobs) {
        final Dialog dialog = new Dialog(activity);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_add_shift);
        final AutoCompleteTextView AutoTextJobName =(AutoCompleteTextView) dialog.findViewById(R.id.editShiftName);
        final EditText shiftStartDate = dialog.findViewById(R.id.editShiftStartDate);
        final EditText shiftTimeStart = dialog.findViewById(R.id.editShiftStartTime);
        final EditText shiftEndDate = dialog.findViewById(R.id.editShiftEndDate);
        final EditText shiftTimeEnd = dialog.findViewById(R.id.editShiftEndTime);
        updateDateTime(shiftStartDate, shiftTimeStart, shiftEndDate, shiftTimeEnd);
        updateDropDownJobsName(AutoTextJobName);
        Button submitButton = dialog.findViewById(R.id.btnNewShiftSave);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = AutoTextJobName.getText().toString();
                if (!jobs.contains(name)) {
                    popUpMessage("המשרה המבוקשת לא קיימת");
                    return;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                try {
                    LocalDateTime shift_start = LocalDateTime.parse(shiftStartDate.getText().toString() + " " + shiftTimeStart.getText().toString(), formatter);
                    LocalDateTime shift_end = LocalDateTime.parse(shiftEndDate.getText().toString() + " " + shiftTimeEnd.getText().toString(), formatter);
                    if (shift_end.isBefore(shift_start)){
                        popUpMessage("הנתונים שהוזנו אינם תקינים");
                        return;
                    }
                    if (Validate.isValidDateTime(shift_start,shift_end)) {
                        Shift shift = new Shift(shift_start, shift_end, currentUser.getMail(), name);
                        DB.setInShifts(shift);
                        popUpMessage(R.string.shift_added_successfully);
                        showListOfShifts();
                        dialog.dismiss();
                    }
                    else throw new Exception();
                } catch (Exception e) {
                    popUpMessage("הנתונים שהוזנו אינם תקינים");
                    return;
                }
            }
        });
        dialog.show();
    }



    private void updateDropDownJobsName(AutoCompleteTextView jobName) {
        DB.getJobs(currentUser.getMail(), new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobsNames) {
                String[] jobsName = new String[jobsNames.size()];
                for (int i = 0; i < jobsNames.size(); i++) {
                    jobsName[i] = jobsNames.get(i);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (activity,android.R.layout.select_dialog_item,jobsName);
                jobName.setAdapter(adapter);
                jobName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            jobName.showDropDown();
                    }
                });
                jobName.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        jobName.showDropDown();
                        return false;
                    }
                });
            }
        });

    }

    public void addJob() {
        final Dialog dialog = new Dialog(activity);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_add_job);
        final EditText jobName = dialog.findViewById(R.id.editJobName);
        final EditText hourSal = dialog.findViewById(R.id.editHourPay);
        final EditText bossId = dialog.findViewById(R.id.editBossMail);
        Button submitButton = dialog.findViewById(R.id.btnNewJobSave);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = jobName.getText().toString();
                if (!Validate.isValidInput(name)) {
                    popUpMessage("שם המשרה לא תקין");
                    return;
                }
                String hourPay = hourSal.getText().toString();
                if (!Validate.isNumeric(hourPay)) {
                    popUpMessage("השכר שהוזן אינו תקין");
                    return;
                }
                String bossMail = bossId.getText().toString();
                if (!Validate.isValidEmail(bossMail)) {
                    popUpMessage("המייל שהוזן עבור המנהל אינו תקין");
                    return;
                }
                DB.CheckIfTheUserMailIsExists(bossMail, new Callback<Boolean>() {
                    @Override
                    public void play(Boolean isExits) {
                        if (!isExits){
                            popUpMessage("המייל שהוזן עבור המנהל לא קיים במערכת");
                            return;
                        }
                        Job job = new Job(bossMail, hourPay, currentUser.getMail(), name);
                        String title = "שלום, נוסף לך עובד חדש";
                        String message = getMessageNotification(currentUser.getUserName(),name,hourPay);
                        sendNotificationToUserMail(bossMail,title,message);
                        DB.setInJobs(job);
                        popUpMessage(R.string.job_added_success);
                        showListOfShifts();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

}
