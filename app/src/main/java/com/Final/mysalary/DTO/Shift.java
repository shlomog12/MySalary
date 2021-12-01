package com.Final.mysalary.DTO;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Shift {

    LocalDateTime dateTimeStart;
    LocalDateTime dateTimeEnd;
    int jobId;
    double totalHours;
    double totalSalary;
    String userName;




    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(LocalDateTime start, LocalDateTime end , String userName, int jobId) {
        this.dateTimeStart =start;
        this.dateTimeEnd = end;
        this.totalHours = end.until(start, ChronoUnit.HOURS);
        this.userName = userName;
        this.jobId = jobId;
        DB.getSalaryForJob(this.userName, this.jobId, new Callback<Double>() {
            @Override
            public void play(Double num) {
                updateSalary(num);
            }
        });
    }

    private void updateSalary(double num) {
        double salaryForHour = num;
        this.totalSalary = salaryForHour*this.totalHours;
    }


    public LocalDateTime getDateTimeStart() {
        return dateTimeStart;
    }

    public void setDateTimeStart(LocalDateTime dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    public LocalDateTime getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(LocalDateTime dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
