package com.Final.mysalary.Controller.Actions;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.ShiftsAdapter;
import com.Final.mysalary.Controller.date.DatePickerFragment;
import com.Final.mysalary.Controller.date.TimePickerFragment;
import com.Final.mysalary.R;
import com.Final.mysalary.UI.LoginActivity;
import com.Final.mysalary.Model.DTO.Shift;
import com.Final.mysalary.Model.DTO.Type;
import com.Final.mysalary.Model.DTO.User;
import com.Final.mysalary.UI.BossActivity;
import com.Final.mysalary.UI.WorkerActivity;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Controller.notfication.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class UiActions {

    protected User currentUser;
    boolean ShowSalary = true;
    LocalDateTime shift_start = LocalDateTime.MIN;
    LocalDateTime shift_end = LocalDateTime.MAX;

    protected final AppCompatActivity activity;
    protected FirebaseAuth mAuth;

    public UiActions(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }
    public void moveToMainScreen(User curUser) {
        if (curUser == null) return;
        DB.updateToken(curUser.getMail());
        Intent intent;
        if (curUser.getType() == Type.WORKER.ordinal())
            intent = new Intent(activity, WorkerActivity.class);
        else intent = new Intent(activity, BossActivity.class);
        intent.putExtra("userMail", curUser.getMail());
        activity.startActivity(intent);
    }
    public void popUpMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
    public void popUpMessage(int message) {
        String messageStr = activity.getApplicationContext().getString(message);
        Toast.makeText(activity, messageStr, Toast.LENGTH_LONG).show();
    }
    private void sendNotificationToTokenId(String tokenId, String title, String message){
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(tokenId,title
                ,message, activity.getApplicationContext(),activity);
        notificationsSender.SendNotifications();
    }
    public void sendNotificationToUserMail(String userMail, String title, String message){
        DB.getTokenIdByUserMail(userMail, new Callback<String>() {
            @Override
            public void play(String tokenId) {
                sendNotificationToTokenId(tokenId,title,message);
            }
        });
    }
    public String getMessageNotification(String userName, String name, String hourPay) {
        String s1 =activity.getApplicationContext().getString(R.string.the_user);
        String s2 = activity.getApplicationContext().getString(R.string.added_you_boss);
        String s3 = activity.getApplicationContext().getString(R.string.in_salary);
        String s4 = activity.getApplicationContext().getString(R.string.per_hour);
        return s1+" "+userName+" "+s2+" "+name +" "+ s3+": "+hourPay +" "+ s4;
    }
    public void ChangeSum() {
        TextView ShowHoursOrSalary = activity.findViewById(R.id.SumSalaryBar);
        ShowHoursOrSalary.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (ShowSalary) {
                    ShowHoursOrSalary.setText(R.string.sum_payment);
                    ShowSalary = false;
                } else {
                    ShowHoursOrSalary.setText(R.string.total_hours);
                    ShowSalary = true;
                }
                showListOfShifts();
            }
        });
    }
    public void showListOfShifts(){};
    public String getUserMail() {
        String userMail;
        Bundle extras = activity.getIntent().getExtras();
        if (extras != null) {
            userMail = extras.getString("userMail");
            if (userMail != null) return userMail;
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userMail = firebaseUser.getEmail();
        return userMail;
    }
    protected void updateDateTime(EditText shiftStartDate, EditText shiftTimeStart, EditText shiftEndDate, EditText shiftTimeEnd) {
        updateDate(shiftStartDate);
        updateDate(shiftEndDate);
        updateTime(shiftTimeStart);
        updateTime(shiftTimeEnd);
    }
    public void updateDate(EditText shiftDate) {
        DatePickerFragment date = new DatePickerFragment();
        shiftDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(date, shiftDate);
            }
        });
        shiftDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) showDate(date, shiftDate);
            }
        });
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
        time.show(activity.getSupportFragmentManager(), "timePicker");
        time.setEdit(shiftTime);
    }
    public void showDate(DatePickerFragment date, EditText shiftDate) {
        date.show(activity.getSupportFragmentManager(), "datePicker");
        date.setEdit(shiftDate);
    }
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        signOutFromGoogle();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }
    public void signOutFromGoogle() {
        LoginActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        popUpMessage(R.string.bye);
                    }
                });
    }
    public void updateUser() {
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

}