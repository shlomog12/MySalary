package com.Final.mysalary.DTO;

public class Job {

    String mailBoss;
    String salaryForHour;
    String mailWorker;
    String jobName;


    public Job(){}
    public Job(String mailBoss, String salaryForHour, String mailWorker, String jobName) {
        this.mailBoss = mailBoss;
        this.salaryForHour = salaryForHour;
        this.mailWorker = mailWorker;
        this.jobName = jobName;
    }

    public String getMailWorker() {
        return mailWorker;
    }
    public void setMailWorker(String mailWorker) {
        this.mailWorker = mailWorker;
    }
    public String getMailBoss() {
        return mailBoss;
    }
    public void setMailBoss(String mailBoss) {
        this.mailBoss = mailBoss;
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
                "mailBoss='" + mailBoss + '\'' +
                ", salaryForHour='" + salaryForHour + '\'' +
                ", MailWorker='" + mailWorker + '\'' +
                ", jobName='" + jobName + '\'' +
                '}';
    }
}
