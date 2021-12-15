package com.Final.mysalary.DTO;


import java.util.Locale;

public class User {

    private String firstName;
    private String lastName;
    private String mail;
    private String password;
    private String userName;
    private Type type;


    public User(String mail, String firstName, String lastName , String password, String userName, Type type) {
        this.mail = mail.toLowerCase(Locale.ROOT);
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userName= userName;
        this.type = type;
    }

    public User(){}

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String Password() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail.toLowerCase(Locale.ROOT);;;
    }
    public int getType() {
        return this.type.ordinal();
    }
    public void setType(int type) {
        this.type = (type == 0) ? Type.WORKER : Type.BOSS;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", type=" + type +
                '}';
    }
}
