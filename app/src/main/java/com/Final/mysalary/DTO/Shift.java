package com.Final.mysalary.DTO;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Shift {

    LocalDateTime start;
    LocalDateTime end;
    int jobId;
    double totalHours;
    double totalSalary;
    String userName;


    public Shift(){ }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(LocalDateTime start, LocalDateTime end , String userName, int jobId) {
        this.start =start;
        this.end = end;
        this.userName = userName;
        this.jobId = jobId;
        updateTotalSalary();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateTotalSalary() {
        updateTotalHours();
        if (this.userName ==null) return;
        DB.getSalaryForJob(this.userName, this.jobId, new Callback<String>() {
            @Override
            public void play(String salary) {
                updateSalary(salary);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateTotalHours() {
        double totalMinutes = this.start.until(this.end, ChronoUnit.MINUTES);
        this.totalHours = totalMinutes/60;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStartTime(LocalDateTime start) {
        this.start = start;
        if (this.end!= null) updateTotalSalary();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStart(String start) {
        this.setStartTime(LocalDateTime.parse(start));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEndTime(LocalDateTime end) {
        this.end = end;
        if (this.start!= null) updateTotalSalary();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEnd(String end) {
        this.setEndTime(LocalDateTime.parse(end));
    }
    public void setJobIdFromInt(int jobId) {
        this.jobId = jobId;
    }
    public void setJobId(String jobId) {
        this.setJobIdFromInt(Integer.parseInt(jobId));
    }
    public LocalDateTime getStart() {
        return start;
    }
    public LocalDateTime getEnd() {
        return end;
    }
    private void updateSalary(String salary) {
        double salaryForHour = Double.parseDouble(salary);
        this.totalSalary = salaryForHour*this.totalHours;
    }
    public int getJobId() {
        return jobId;
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
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public String toString() {
        return "Shift{" +
                "start=" + start +
                ", end=" + end +
                ", jobId=" + jobId +
                ", totalHours=" + totalHours +
                ", totalSalary=" + totalSalary +
                ", userName='" + userName + '\'' +
                '}';
    }

}
