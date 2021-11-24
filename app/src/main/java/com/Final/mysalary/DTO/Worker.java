package com.Final.mysalary.DTO;

public class Worker implements User {

    String mail;
    String userName;
    String firstName;
    String lastName;
    String password;

    public Worker(String mail, String firstName,String lastName, String password, String userName) {
        this.mail = mail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {

    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String firstName) {

    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public String getMail() {
        return this.mail;
    }

    @Override
    public void setMail(String mail) {

    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String lastName) {

    }


}