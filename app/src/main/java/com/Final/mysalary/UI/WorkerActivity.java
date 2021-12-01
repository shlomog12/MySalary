package com.Final.mysalary.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.Final.mysalary.db.DB;
import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;

public class WorkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
    }

    private void showTotalSumOfSalary() { }
    private void showListOfShifts() {}


    private void addJob() {
        openWindowAddJob();
        Job newJob = new Job(getUserNameWarker(),getUserNameBoss(),0);
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
    private void addNewShift(){
        popUpAddShiftWindow();
        Shift shift = new Shift(getStart(),getEnd(),getMail(), getJobId());
        DB.setInShifts(shift);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StartAndEndShift(){
        if (ButtonIsStart()){
            Shift shift = new Shift(LocalDateTime.now(),null,getMail(), getJobId());
            DB.setInShifts(shift);
        }else updateEndOfShift();
    }
    private void sortShifts() { }







    private void updateEndOfShift() { }
    private boolean ButtonIsStart() {
        return false;
    }
    private int getJobId() { return 0; }
    private String getMail() {
        return "";
    }
    private LocalDateTime getEnd() {
        return null;
    }
    private LocalDateTime getStart() {return null; }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveShift() {
//        LocalDateTime start = LocalDateTime.of(2021,11,15,21,22);
//        LocalDateTime end = LocalDateTime.of(2021,11,15,21,22);
        Shift shift = new Shift(null,null,"0",0);
        DB.setInShifts(shift);
        closeAddShiftWindow();
    }

    private void popUpAddShiftWindow() {
    }
    private void closeAddShiftWindow() { }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class));
    }
}