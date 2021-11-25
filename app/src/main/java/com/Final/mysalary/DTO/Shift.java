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
    double salary;
    String userName;




    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(LocalDateTime start, LocalDateTime end , String userName, int jobId) {
        this.dateTimeStart =start;
        this.dateTimeEnd = end;
        this.totalHours = end.until(start, ChronoUnit.HOURS);
        this.userName = userName;
        this.jobId = jobId;
        DB.getSalaryForJob(this.userName, this.jobId, new Callback() {
            @Override
            public void play(User user) {
            }

            @Override
            public void play(boolean bool) {
            }

            @Override
            public void play(ArrayList<Shift> shifts) {
            }

            @Override
            public void play(double num) {
                updateSalary(num);
            }
        });
    }

    private void updateSalary(double num) {
        double salaryForHour = num;
        this.salary = salaryForHour*this.totalHours;
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
