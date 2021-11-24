package com.Final.mysalary.DTO;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.Final.mysalary.DB;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Shift {

    LocalDateTime dateTimeStart;
    LocalDateTime dateTimeEnd;
    int jobId;
    double totalHours;
    double salary;
    String userName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(LocalDateTime start, LocalDateTime end , String userName, int jobId) {
        this.dateTimeStart =start;
        this.dateTimeEnd = end;
        this.totalHours = end.until(start, ChronoUnit.HOURS);
        this.jobId = jobId;
        updateSalary();
    }

    private void updateSalary() {
        double salaryForHour = DB.getSalaryForJob(this.userName,this.jobId);
        this.salary = salaryForHour*this.totalHours;
    }


}
