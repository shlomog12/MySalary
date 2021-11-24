package com.Final.mysalary.DTO;

public class Boss implements User{

    String lastNmae;
    String mail;
    String userName;
    String firstName;
    String password;

    public Boss(String mail, String firstName,String lastName ,String password, String userName) {
        this.mail = mail;
        this.firstName = firstName;
        this.lastNmae = lastName;
        this.password = password;
        this.userName= userName;
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
        return this.lastNmae;
    }

    @Override
    public void setLastName(String lastName) {

    }
}
