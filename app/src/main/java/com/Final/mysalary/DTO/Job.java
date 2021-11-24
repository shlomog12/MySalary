package com.Final.mysalary.DTO;

public class Job {

    String userNameWorker;
    String userNameBoss;
    double salary;
    int jobId;

    public Job(String userNameWorker, String userNameBoss, double salary) {
        this.userNameWorker = userNameWorker;
        this.userNameBoss = userNameBoss;
        this.salary = salary;
        this.jobId = -1;
    }

    public String getUserNameWorker() {
        return userNameWorker;
    }
    public void setUserNameWorker(String userNameWorker) {
        this.userNameWorker = userNameWorker;
    }
    public String getUserNameBoss() {
        return userNameBoss;
    }
    public void setUserNameBoss(String userNameBoss) {
        this.userNameBoss = userNameBoss;
    }
    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }
    public int getJobId() {
        return jobId;
    }
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
