package com.Final.mysalary.UI;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.Final.mysalary.DTO.*;
import com.Final.mysalary.R;
import com.Final.mysalary.db.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class WorkerActivity extends AppCompatActivity {

    ShiftsAdapter myAdapter;
    FirebaseAuth mAuth;
    User currentUser;


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
        String userName = updateUserName();
        if (userName == null) return;
        DB.getUserByUserName(userName, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                currentUser = user;
                showListOfShifts();
            }
        });
    }
    private String updateUserName() {
        String userName;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            userName = extras.getString("user");
            if (userName != null) return userName;
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        return userName;
    }

    private void showTotalSumOfSalary() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showListOfShifts() {
        if (currentUser == null){ return; }
        DB.getShifts(-1, LocalDateTime.MIN, LocalDateTime.MAX, currentUser.getUserName(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(WorkerActivity.this, shifts);
                ListView shiftsListView = findViewById(R.id.listView);
                shiftsListView.setAdapter(shiftsArrayAdapter);
            }
        });
    }


    private void addJob() {
        openWindowAddJob();
        Job newJob = new Job(getUserNameWarker(), getUserNameBoss(), "22");
        DB.setInJobs(newJob);
        closeWindowAddJob();
    }

    private int getJobIdFromScrean() {
        return 0;
    }

    private String getUserNameBoss() {
        return "";
    }

    private String getUserNameWarker() {
        return "";
    }

    private void closeWindowAddJob() {

    }

    private void openWindowAddJob() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNewShift() {
        popUpAddShiftWindow();
        Shift shift = new Shift(getStart(), getEnd(), getMail(), getJobId());
        DB.setInShifts(shift);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StartAndEndShift() {
        if (ButtonIsStart()) {
            Shift shift = new Shift(LocalDateTime.now(), null, getMail(), getJobId());
            DB.setInShifts(shift);
        } else updateEndOfShift();
    }

    private void sortShifts() {
    }


    private void updateEndOfShift() {
    }

    private boolean ButtonIsStart() {
        return false;
    }

    private int getJobId() {
        return 0;
    }

    private String getMail() {
        return "";
    }

    private LocalDateTime getEnd() {
        return null;
    }

    private LocalDateTime getStart() {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveShift() {
//        LocalDateTime start = LocalDateTime.of(2021,11,15,21,22);
//        LocalDateTime end = LocalDateTime.of(2021,11,15,21,22);
        Shift shift = new Shift(null, null, "0", 0);
        DB.setInShifts(shift);
        closeAddShiftWindow();
    }

    private void popUpAddShiftWindow() {
    }

    private void closeAddShiftWindow() {
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));

    }














//private.
/*
play(userName){
print
    user.userName = userName;
}




 */






//    DB.getUserName(play);


































}