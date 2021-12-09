package com.Final.mysalary.DTO;

public class Job {

    String userNameBoss;
    String salaryForHour;
    String userNameWorker;


    int jobId;

//    public Job(String userNameBoss, String salaryForHour) {
//        this.userNameBoss = userNameBoss;
//        this.salaryForHour = salaryForHour;
//    }

    public Job(){}
    public Job(String userNameWorker, String userNameBoss, String salaryForHour) {
        this.userNameWorker = userNameWorker;
        this.userNameBoss = userNameBoss;
        this.salaryForHour = salaryForHour;
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
    public int getJobId() {
        return jobId;
    }
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
    public String getSalaryForHour() {
        return salaryForHour;
    }
    public void setSalaryForHour(String salaryForHour) {
        this.salaryForHour = salaryForHour;
    }
    @Override
    public String toString() {
        return "Job{" +
                "userNameWorker='" + userNameWorker + '\'' +
                ", userNameBoss='" + userNameBoss + '\'' +
                ", salaryForHour='" + salaryForHour + '\'' +
                ", jobId=" + jobId +
                '}';
    }
}
