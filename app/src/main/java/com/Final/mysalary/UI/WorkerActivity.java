package com.Final.mysalary.UI;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.ShiftsAdapter;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.UI.date.DatePickerFragment;
import com.Final.mysalary.UI.date.TimePickerFragment;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WorkerActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    User currentUser;
    final boolean[] ShowSalary = {true};
    UiActions actions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
    }
    public void onStart() {
        super.onStart();
        updateUser();
    }

    private void updateUser() {
        String userMail = getUserMail();
        if (userMail == null) return;
        DB.getUserByUserMail(userMail, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                currentUser = user;
                ChangeSum();
                showListOfShifts();
            }
        });
    }
    private String getUserMail() {
        String userMail;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userMail = extras.getString("userMail");
            if (userMail != null) return userMail;
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userMail = firebaseUser.getEmail();
        return userMail;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_shift:
                DB.getJobs(currentUser.getMail(),new Callback<ArrayList<String>>() {
                    @Override
                    public void play(ArrayList<String> jobs) {
                        addNewShift(jobs);
                    }
                });

                return true;
            case R.id.menu_add_job:
                addJob();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void addJob() {
        final Dialog dialog = new Dialog(WorkerActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.popup_add_job);

        //Initializing the views of the dialog.
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
                    actions.popUpMessage("שם המשרה לא תקין");
                    return;
                }
                String hourPay = hourSal.getText().toString();
                if (!Validate.isNumeric(hourPay)) {
                    actions.popUpMessage("השכר שהוזן אינו תקין");
                    return;
                }
                String bossMail = bossId.getText().toString();
                if (!Validate.isValidEmail(bossMail)){
                    actions.popUpMessage("המייל שהוזן עבור המנהל אינו תקין");
                    return;
                }
                Job job = new Job(bossMail, hourPay, currentUser.getMail(), name);
                DB.setInJobs(job);
                actions.popUpMessage(String.valueOf(R.string.job_added_success));
                showListOfShifts();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void ChangeSum(){
        TextView ShowHoursOrSalary = findViewById(R.id.SumSalaryBar);
        ShowHoursOrSalary.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (ShowSalary[0]) {
                    ShowHoursOrSalary.setText(R.string.sum_payment);
                    ShowSalary[0] = false;
                }
                else{
                    ShowHoursOrSalary.setText(R.string.total_hours);
                    ShowSalary[0] = true;
                }
                showListOfShifts();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showListOfShifts() {
        if (currentUser == null) {
            return;
        }
        DB.getShifts("", LocalDateTime.MIN, LocalDateTime.MAX, currentUser.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(WorkerActivity.this, shifts);
                shiftsArrayAdapter.setShowSalary(ShowSalary[0]);
                ListView shiftsListView = findViewById(R.id.listView);
                shiftsListView.setAdapter(shiftsArrayAdapter);
                double totalsum = 0;
                double totalHr = 0;
                for (Shift s : shifts) {
                    totalsum += s.TotalSalary();
                    totalHr += s.TotalHours();
                }
                TextView sum = findViewById(R.id.sumSalary);
                sum.setText(getApplicationContext().getString(R.string.sum_payment) + " " + String.format("%.2f", totalsum) + "\n" + getApplicationContext().getString(R.string.total_hours) + " " + String.format("%.2f", totalHr));
            }
        });
    }
    private void addNewShift(ArrayList<String> jobs) {
        final Dialog dialog = new Dialog(WorkerActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.popup_add_shift);

        //Initializing the views of the dialog.
        final EditText jobName = dialog.findViewById(R.id.editShiftName);
        final EditText shiftStartDate = dialog.findViewById(R.id.editShiftStartDate);
        final EditText shiftTimeStart = dialog.findViewById(R.id.editShiftStartTime);
        final EditText shiftEndDate = dialog.findViewById(R.id.editShiftEndDate);
        final EditText shiftTimeEnd = dialog.findViewById(R.id.editShiftEndTime);
        updateDateTime(shiftStartDate,shiftTimeStart,shiftEndDate,shiftTimeEnd);
        Button submitButton = dialog.findViewById(R.id.btnNewShiftSave);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = jobName.getText().toString();
                if (!jobs.contains(name)) {
                    actions.popUpMessage("המשרה המבוקשת לא קיימת");
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                try {
                    LocalDateTime shift_start = LocalDateTime.parse(shiftStartDate.getText().toString() + " " + shiftTimeStart.getText().toString(), formatter);
                    LocalDateTime shift_end = LocalDateTime.parse(shiftEndDate.getText().toString() + " " + shiftTimeEnd.getText().toString(), formatter);
                    Shift shift = new Shift(shift_start, shift_end, currentUser.getMail(), name);
                    DB.setInShifts(shift);
                    actions.popUpMessage(String.valueOf(R.string.shift_added_successfully));
                    dialog.dismiss();
                    showListOfShifts();
                }catch (Exception e){
                    actions.popUpMessage("הנתונים שהוזנו אינם תקינים");
                    return;
                }
                
            }
        });
        dialog.show();
    }

    private void updateDateTime(EditText shiftStartDate, EditText shiftTimeStart, EditText shiftEndDate, EditText shiftTimeEnd) {
        updateDate(shiftStartDate);
        updateDate(shiftEndDate);
        updateTime(shiftTimeStart);
        updateTime(shiftTimeEnd);
    }
    private void updateTime(EditText shiftTime) {
        TimePickerFragment time = new TimePickerFragment();
        shiftTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime(time, shiftTime);
            }
        });
        shiftTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) showTime(time, shiftTime);
            }
        });
    }
    private void showTime(TimePickerFragment time, EditText shiftTime) {
        time.show(getSupportFragmentManager(), "timePicker");
        time.setEdit(shiftTime);
    }
    private void updateDate(EditText shiftDate) {
        DatePickerFragment date = new DatePickerFragment();
        shiftDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(date,shiftDate);
            }
        });
            shiftDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) showDate(date,shiftDate);
                }
            });
        }private void showDate(DatePickerFragment date, EditText shiftDate) {
            date.show(getSupportFragmentManager(), "datePicker");
            date.setEdit(shiftDate);
        }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        signOutFromGoogle();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void signOutFromGoogle() {
        LoginActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        actions.popUpMessage(String.valueOf(R.string.bye));
                    }
                });
    }
}