package com.Final.mysalary.DTO;

public class Job {

    String userNameBoss;
    String salaryForHour;
    String userNameWorker;
    String jobName;


    public Job(){}
    public Job(String userNameBoss, String salaryForHour, String userNameWorker, String jobName) {
        this.userNameBoss = userNameBoss;
        this.salaryForHour = salaryForHour;
        this.userNameWorker = userNameWorker;
        this.jobName = jobName;
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
    public String getSalaryForHour() {
        return salaryForHour;
    }
    public void setSalaryForHour(String salaryForHour) {
        this.salaryForHour = salaryForHour;
    }
    public String JobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String toString() {
        return "Job{" +
                "userNameBoss='" + userNameBoss + '\'' +
                ", salaryForHour='" + salaryForHour + '\'' +
                ", userNameWorker='" + userNameWorker + '\'' +
                ", jobName='" + jobName + '\'' +
                '}';
    }
}
