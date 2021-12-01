package com.Final.mysalary.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Final.mysalary.db.DB;
import com.Final.mysalary.DTO.Boss;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.DTO.Worker;
import com.Final.mysalary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    boolean validate;
    boolean isBoss;
    User newUser;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
    }



    public void moveToLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    public void clean(View view){
        ((EditText)findViewById(R.id.editMail)).setText("", TextView.BufferType.EDITABLE);
        ((EditText)findViewById(R.id.editUserName)).setText("", TextView.BufferType.EDITABLE);
        ((EditText)findViewById(R.id.editFirstName)).setText("", TextView.BufferType.EDITABLE);
        ((EditText)findViewById(R.id.editLastName)).setText("", TextView.BufferType.EDITABLE);
        ((EditText)findViewById(R.id.editPassword)).setText("", TextView.BufferType.EDITABLE);
        ((EditText)findViewById(R.id.editValidPassword)).setText("", TextView.BufferType.EDITABLE);
    }
    public void close(View view){
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
        updateTypeUser();
        if (!validate) return;
        if (isBoss) newUser = new Boss(mail,firstName,lastName,password,userName);
        else newUser = new Worker(mail, firstName, lastName, password, userName);
        DB.CheckIfTheUserNameIsBusy(userName, new Callback<Boolean>() {
            @Override
            public void play(Boolean isBusy) {
                sendFeedbackToUser(isBusy);
            }
        });
    }
    public void sendFeedbackToUser(boolean busyUserName){
        if (busyUserName){
            popUpMessage("שם משתמש תפוס, אנא בחר שם אחר");
            return;
        }
        registerWithFireBase();
    }
    private void updateTypeUser() {
        RadioGroup radioGroup =  findViewById(R.id.radioGroupReg);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (radioButton == null){
            popUpMessage("אנא בחר סוג משתמש");
            validate = false;
        }
        isBoss = (radioButton == findViewById(R.id.radBtnBossReg));
    }

    private String getUserNameFromScreen() {
        String userName = ((EditText)findViewById(R.id.editUserName)).getText().toString();
        if (!validUserName(userName)){
            validate = false;
            return null;
        }
        return userName;
    }
    private boolean validUserName(String userName){
        if (userName.length() < 1){
            popUpMessage("שם משתמש קצר מדיי");
            return false;
        }
        return true;
    }
    private String getMailFromScreen() {
        String mail = ((EditText)findViewById(R.id.editMail)).getText().toString();
        if (!isValidEmail(mail)){
            validate = false;
            popUpMessage("כתובת מייל לא תקינה");
            return null;
        }
        return mail;
    }
    private boolean isValidEmail(String mail) {
        return (!TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches());
    }
    private String getFirstNameFromScreen() {
        String firstName = ((EditText)findViewById(R.id.editFirstName)).getText().toString();
        if (!validFirstName(firstName)) {
            popUpMessage("שם פרטי לא תקין");
            validate = false;
            return null;
        }
        return firstName;
    }
    private boolean validFirstName(String firstName) {
        if (firstName.length() < 1) return false;
        return true;
    }
    private String getLastNameFromScreen() {
        String lastName = ((EditText)findViewById(R.id.editLastName)).getText().toString();
        if(!validLastName(lastName)){
            popUpMessage("שם משפחה לא תקין");
            validate = false;
            return null;
        }
        return lastName;
    }
    private boolean validLastName(String lastName) {
        if (lastName.length() < 1) return false;
        return true;
    }
    private String getPasswordFromScreen() {
        String password = ((EditText)findViewById(R.id.editPassword)).getText().toString();
        String validPassword = ((EditText)findViewById(R.id.editValidPassword)).getText().toString();
        passwordCheck(password,validPassword);
        return password;
    }
    private void passwordCheck(String password, String validPassword) {
        if (!isValidPassword(password)){
            popUpMessage("הסימה לא תקינה.\nאורך הסיסמה לפחות 5 תווים.\nבנוסף סיסמה צריכה להכיל אותיות גדולות, סימנים, ומספרים");
            validate = false;
            return;
        }
        if (!password.equals(validPassword)){
            popUpMessage("אימות סיסמה לא תואם לסיסמה");
            validate = false;
            return;
        }
        return;
    }
    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
    private void popUpMessage(String message) {
        Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_LONG).show();
    }

    public  void registerWithFireBase(){

        mAuth.createUserWithEmailAndPassword(newUser.getMail(),newUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            popUpMessage("ההרשמה בוצעה בהצלחה");
                            DB.setUser(newUser);
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUser.getUserName()).build();
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            firebaseUser.updateProfile(profileChangeRequest);
                            moveToMainScrean();
                        }else {
                            System.out.println(task.getException());
                            popUpMessage("הרשמה נכשלה, המייל כבר קיים במערכת");
                        }
                    }
                });
    }

    private void moveToMainScrean() {
        Intent intent;
        if (newUser.getType() == "worker") intent = new Intent(this,WorkerActivity.class);
        else intent = new Intent(this,BossActivity.class);
        startActivity(intent);
    }

}