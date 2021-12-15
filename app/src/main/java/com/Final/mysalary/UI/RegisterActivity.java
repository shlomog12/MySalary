package com.Final.mysalary.UI;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class RegisterActivity extends AppCompatActivity {

    boolean validate;
    User newUser;
    public FirebaseAuth mAuth;
    UiActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        actions = new UiActions(this);
    }
    public void moveToLoginScreen(){
        startActivity(new Intent(this,LoginActivity.class));
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
        Type typeOfUser = getTypeOfUser();
        if (!validate) return;
        newUser = new User(mail,firstName,lastName,password,userName, typeOfUser);
        DB.CheckIfTheUserMailIsExists(userName, new Callback<Boolean>() {
            @Override
            public void play(Boolean isBusy) {
                sendFeedbackToUser(isBusy);
            }
        });



    }
    public void sendFeedbackToUser(boolean busyUserName){
        if (busyUserName){
            actions.popUpMessage("שם משתמש תפוס, אנא בחר שם אחר");
            return;
        }
        registerWithFireBase();
    }
    private Type getTypeOfUser() {
        RadioGroup radioGroup =  findViewById(R.id.radioGroupReg);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (radioButton == null){
            actions.popUpMessage("אנא בחר סוג משתמש");
            validate = false;
        }
        if (radioButton == findViewById(R.id.radBtnBossReg)) return Type.BOSS;
        return Type.WORKER;
    }
    private String getUserNameFromScreen() {
        String userName = ((EditText)findViewById(R.id.editUserName)).getText().toString();
        if (!Validate.isValidInput(userName)){
            actions.popUpMessage("שם משתמש קצר מדיי");
            validate = false;
            return null;
        }
        return userName;
    }
    private String getMailFromScreen() {
        String mail = ((EditText)findViewById(R.id.editMail)).getText().toString();
        if (!Validate.isValidEmail(mail)){
            validate = false;
            actions.popUpMessage("כתובת מייל לא תקינה");
            return null;
        }
        return mail;
    }
    private String getFirstNameFromScreen() {
        String firstName = ((EditText)findViewById(R.id.editFirstName)).getText().toString();
        if (!Validate.isValidInput(firstName)) {
            actions.popUpMessage("שם פרטי לא תקין");
            validate = false;
            return null;
        }
        return firstName;
    }
    private String getLastNameFromScreen() {
        String lastName = ((EditText)findViewById(R.id.editLastName)).getText().toString();
        if(!Validate.isValidInput(lastName)){
            actions.popUpMessage("שם משפחה לא תקין");
            validate = false;
            return null;
        }
        return lastName;
    }
    private String getPasswordFromScreen() {
        String password = ((EditText)findViewById(R.id.editPassword)).getText().toString();
        String validPassword = ((EditText)findViewById(R.id.editValidPassword)).getText().toString();
        passwordCheck(password,validPassword);
        return password;
    }
    private void passwordCheck(String password, String validPassword) {
        if (!Validate.isValidPassword(password)){
            actions.popUpMessage("הסימה לא תקינה.\nאורך הסיסמה לפחות 6 תווים.\nבנוסף סיסמה צריכה להכיל אותיות גדולות, סימנים, ומספרים");
            validate = false;
            return;
        }
        if (!password.equals(validPassword)){
            actions.popUpMessage("אימות סיסמה לא תואם לסיסמה");
            validate = false;
            return;
        }
        return;
    }
    public  void registerWithFireBase(){
        mAuth.createUserWithEmailAndPassword(newUser.getMail(),newUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            actions.popUpMessage("ההרשמה בוצעה בהצלחה");
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUser.getUserName()).build();
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            System.out.println(newUser.getUserName());
                            firebaseUser.updateProfile(profileChangeRequest);
                            DB.setUser(newUser);
                            actions.moveToMainScreen(newUser);

                        }else {
                            System.out.println(task.getException());
                            actions.popUpMessage("הרשמה נכשלה, המייל כבר קיים במערכת");
                        }
                    }
                });
    }
}