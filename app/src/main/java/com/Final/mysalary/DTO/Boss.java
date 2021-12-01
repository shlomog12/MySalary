package com.Final.mysalary.DTO;

public class Boss implements User {

    public String first_name;
    public String last_name;
    public String mail;
    public String password;
    public String userName;

    public Boss(String mail, String firstName,String lastName ,String password, String userName) {
        this.mail = mail;
        this.first_name = firstName;
        this.last_name = lastName;
        this.password = password;
        this.userName= userName;
    }

    public Boss(){}


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
    public String toString() {
        return "Boss{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {

    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
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
    public String getType() {
        return "bosses";
    }
}
