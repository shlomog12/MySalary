package com.Final.mysalary.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.VM.Actions.RegisterActions;
import com.Final.mysalary.VM.Validate;
import com.Final.mysalary.Model.DTO.Type;
import com.Final.mysalary.Model.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RegisterActivity extends AppCompatActivity {

    boolean validate;
    User newUser;
    RegisterActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        actions = new RegisterActions(this);
    }
    public void moveToLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void clean(View view) {
        ((EditText) findViewById(R.id.editMail)).setText("", TextView.BufferType.EDITABLE);
        ((EditText) findViewById(R.id.editUserName)).setText("", TextView.BufferType.EDITABLE);
        ((EditText) findViewById(R.id.editFirstName)).setText("", TextView.BufferType.EDITABLE);
        ((EditText) findViewById(R.id.editLastName)).setText("", TextView.BufferType.EDITABLE);
        ((EditText) findViewById(R.id.editPassword)).setText("", TextView.BufferType.EDITABLE);
        ((EditText) findViewById(R.id.editValidPassword)).setText("", TextView.BufferType.EDITABLE);
    }
    public void close(View view) {
        moveToLoginScreen();
    }

    public void saveDetails(View view) {
        validate = true;
        String mail = getMailFromScreen();
        if (!validate) return;
        String firstName = getFirstNameFromScreen();
        if (!validate) return;
        String lastName = getLastNameFromScreen();
        if (!validate) return;
        String password = getPasswordFromScreen();
        if (!validate) return;
        String userName = getUserNameFromScreen();
        if (!validate) return;
        Type typeOfUser = getTypeOfUser();
        if (!validate) return;
        newUser = new User(mail, firstName, lastName, password, userName, typeOfUser);
        DB.CheckIfTheUserMailIsExists(mail, new Callback<Boolean>() {
            @Override
            public void play(Boolean isBusy) {
                sendFeedbackToUser(isBusy);
            }
        });


    }
    public void sendFeedbackToUser(boolean busyUserName) {
        if (busyUserName) {
            actions.popUpMessage(getApplicationContext().getString(R.string.user_name_used));
            return;
        }
        actions.registerWithFireBase(newUser);
    }
    private Type getTypeOfUser() {
        RadioGroup radioGroup = findViewById(R.id.radioGroupReg);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (radioButton == null) {
            actions.popUpMessage(getApplicationContext().getString(R.string.choose_user_type));
            validate = false;
        }
        if (radioButton == findViewById(R.id.radBtnBossReg)) return Type.BOSS;
        return Type.WORKER;
    }
    private String getUserNameFromScreen() {
        String userName = ((EditText) findViewById(R.id.editUserName)).getText().toString();
        if (!Validate.isValidInput(userName)) {
            actions.popUpMessage(getApplicationContext().getString(R.string.user_name_short));
            validate = false;
            return null;
        }
        return userName;
    }
    private String getMailFromScreen() {
        String mail = ((EditText) findViewById(R.id.editMail)).getText().toString();
        if (!Validate.isValidEmail(mail)) {
            validate = false;
            actions.popUpMessage(getApplicationContext().getString(R.string.mail_incorrect));
            return null;
        }
        return mail;
    }
    private String getFirstNameFromScreen() {
        String firstName = ((EditText) findViewById(R.id.editFirstName)).getText().toString();
        if (!Validate.isValidInput(firstName)) {
            actions.popUpMessage(getApplicationContext().getString(R.string.invalid_first_name));
            validate = false;
            return null;
        }
        return firstName;
    }
    private String getLastNameFromScreen() {
        String lastName = ((EditText) findViewById(R.id.editLastName)).getText().toString();
        if (!Validate.isValidInput(lastName)) {
            actions.popUpMessage(getApplicationContext().getString(R.string.invalid_last_name));
            validate = false;
            return null;
        }
        return lastName;
    }
    private String getPasswordFromScreen() {
        String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        String validPassword = ((EditText) findViewById(R.id.editValidPassword)).getText().toString();
        passwordCheck(password, validPassword);
        return password;
    }
    private void passwordCheck(String password, String validPassword) {
        if (!Validate.isValidPassword(password)) {
            actions.popUpMessage(getApplicationContext().getString(R.string.password_invalid));
            validate = false;
            return;
        }
        if (!password.equals(validPassword)) {
            actions.popUpMessage(getApplicationContext().getString(R.string.verification_wrong));
            validate = false;
            return;
        }
        return;
    }
}