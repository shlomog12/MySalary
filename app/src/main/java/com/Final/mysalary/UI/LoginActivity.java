package com.Final.mysalary.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.Final.mysalary.DB;
import com.Final.mysalary.DTO.Boss;
import com.Final.mysalary.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        if (VerifyUsernameAndPassword()) moveToTheMainScreen();
        else PopUpMessageIncorrectPassword();
    }
    public void loginGoogle(View view){
        moveToTheMainScreen();
    }
    public void forgotThePasswordׂ(View view){ }
    public void moveToTheRegisterScreen(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }




    private void moveToTheMainScreen() {
//      moveToWorkerScreen();
//      moveToBossScreen();
    }

    public void moveToBossScreen(View view) {
        Intent intent = new Intent(this, BossActivity.class);
        startActivity(intent);
    }

    public void moveToWorkerScreen(View view) {
        Intent intent = new Intent(this,WorkerActivity.class);
        startActivity(intent);
    }

    private boolean VerifyUsernameAndPassword() {
        return true;
    }
    private void PopUpMessageIncorrectPassword() {}
}