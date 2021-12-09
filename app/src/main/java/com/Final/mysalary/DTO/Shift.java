package com.Final.mysalary.DTO;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Shift {

    private LocalDateTime start;
    private LocalDateTime end;
    private String jobName;
    private double totalHours;
    private double totalSalary;
    private String userName;



    public Shift(){ }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(LocalDateTime start, LocalDateTime end , String userName, String jobName) {
        this.start =start;
        this.end = end;
        this.userName = userName;
        this.jobName = jobName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateTotalHours() {
        double totalMinutes = this.start.until(this.end, ChronoUnit.MINUTES);
        this.totalHours = totalMinutes/60;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStartTime(LocalDateTime start) {
        this.start = start;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStart(String start) {
        this.setStartTime(LocalDateTime.parse(start));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEndTime(LocalDateTime end) {
        this.end = end;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEnd(String end) {
        this.setEndTime(LocalDateTime.parse(end));
    }
    public LocalDateTime Start() {
        return start;
    }
    public LocalDateTime End() {
        return end;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateSalary(String salary) {
        updateTotalHours();
        double salaryForHour = Double.parseDouble(salary);
        this.totalSalary = salaryForHour*this.totalHours;
    }
    public double TotalHours() {
        return totalHours;
    }
    public void setTotalOfHours(double totalHours) {
        this.totalHours = totalHours;
    }
    public double TotalSalary() {
        return totalSalary;
    }
    public void setTotalOfSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }
    public String UserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String JobName() {
        return jobName;
    }
    public void setJobOfName(String jobName) {
        this.jobName = jobName;
    }
    public String getStart() {
        return start.toString();
    }

    public String getEnd() {
        return end.toString();
    }

    @Override
    public String toString() {
        return "Shift{" +
                "start=" + start +
                ", end=" + end +
                ", jobName='" + jobName + '\'' +
                ", totalHours=" + totalHours +
                ", totalSalary=" + totalSalary +
                ", userName='" + userName + '\'' +
                '}';
    }
}
