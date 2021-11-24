package com.Final.mysalary.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.Final.mysalary.DB;
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
    private void moveToTheRegisterScreen(View view) {
    }


    private void moveToTheMainScreen() { }
    private boolean VerifyUsernameAndPassword() {
        return true;
    }
    private void PopUpMessageIncorrectPassword() {}
}