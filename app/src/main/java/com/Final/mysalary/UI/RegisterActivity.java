package com.Final.mysalary.UI;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.Final.mysalary.DB;
import com.Final.mysalary.DTO.Boss;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.DTO.Worker;
import com.Final.mysalary.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }



    private void close(){
        moveToTheLoginScreen();
    }
    private void saveDetails() {
        if (isBoss()){
            Boss newBoss = new Boss(getMailFromScreen(), getFirstNameFromScreen(),getLastNameFromScreen(), getPasswordFromScreen(), getUserNameFromScreen());
            if (!verificationOfDetails(newBoss)) return;
            DB.setInBosses(newBoss);
        }
        else {
            Worker newWorker = new Worker(getMailFromScreen(), getFirstNameFromScreen(),getLastNameFromScreen(), getPasswordFromScreen(), getUserNameFromScreen());
            if (!verificationOfDetails(newWorker)) return;
            DB.setInWorkers(newWorker);
        }
        moveToTheLoginScreen();
    }




    private boolean verificationOfDetails(User newUser) {
        if(!DB.validNewUser(newUser)){
            popUpTheMessageUserNameIsInvalid();
            return false;
        }
        if(!validatePassword()) {
            popUpTheMessagePasswordIsInvalid();
            return false;
        }
        return true;
    }



    private boolean isBoss() {
        return false;
    }
    private String getUserNameFromScreen() {
        return "";
    }
    private String getPasswordFromScreen() {
        return "";
    }
    private String getMailFromScreen() {
        return "";
    }
    private String getLastNameFromScreen() {
        return "";
    }
    private String getFirstNameFromScreen() {
        return "";
    }

    private void popUpTheMessageUserNameIsInvalid() {

    }
    private void popUpTheMessagePasswordIsInvalid() { }


    private boolean validatePassword() { return true; }
    private void moveToTheLoginScreen(){}












}