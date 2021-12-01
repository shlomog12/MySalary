package com.Final.mysalary.DTO;

public class Worker implements User {


    public String first_name;
    public String last_name;
    public String mail;
    public String password;
    public String userName;


    @Override
    public String toString() {
        return "Worker{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public Worker(){

    }


    public Worker(String mail, String firstName, String lastName, String password, String userName) {
        this.mail = mail;
        this.first_name = firstName;
        this.last_name = lastName;
        this.password = password;
        this.userName = userName;
    }

    public Worker(Worker user) {
        this.mail = user.getMail();
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name();
        this.password = user.getPassword();
        this.userName = user.getUserName();
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getFirst_name() {
        return first_name;
    }

    @Override
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @Override
    public String getLast_name() {
        return last_name;
    }

    @Override
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Override
    public String getType() {
        return "workers";
    }

    @Override
    public String getMail() {
        return mail;
    }

    @Override
    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }



}