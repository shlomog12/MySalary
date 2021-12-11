package com.Final.mysalary.UI;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.Final.mysalary.DTO.*;
import com.Final.mysalary.R;
import com.Final.mysalary.db.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class WorkerActivity extends AppCompatActivity {

    ShiftsAdapter myAdapter;
    FirebaseAuth mAuth;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
    }
    public void onStart() {
        super.onStart();
        updateUser();
    }
    private void updateUser() {
        String userMail = getUserMail();
        if (userMail == null) return;
        DB.getUserByUserMail(userMail, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                currentUser = user;
                showListOfShifts();
            }
        });
    }
    private String getUserMail() {
        String userMail;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            userMail = extras.getString("userMail");
            if (userMail != null) return userMail;
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userMail = firebaseUser.getEmail();
        return userMail;
    }
    private void showTotalSumOfSalary() {
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showListOfShifts() {
        if (currentUser == null){ return; }
        DB.getShifts("", LocalDateTime.MIN, LocalDateTime.MAX, currentUser.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(WorkerActivity.this, shifts);
                ListView shiftsListView = findViewById(R.id.listView);
                shiftsListView.setAdapter(shiftsArrayAdapter);
            }
        });
    }
    public void addJob(View view) {
        final EditText inputJobName =  new EditText(this);
        final EditText inputBossMail=  new EditText(this);
        final EditText inputSalaryForHour =  new EditText(this);
        LinearLayout linearLayout = getLinearLayoutOfAddJobs(inputJobName,inputBossMail,inputSalaryForHour);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("הוסף משרה חדשה")
                .setView(linearLayout)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        String salary = inputSalaryForHour.getText().toString();
                        String mailBoss = inputBossMail.getText().toString();
                        String jobName = inputJobName.getText().toString();
                        if (!isValidInput(jobName)){
                            popUpMessage("שם המשרה שהוזן לא תקין");
                            return;
                        }
                        if (!isNumeric(salary)){
                            popUpMessage("השכר שהוזן לא תקין");
                            return;
                        }
                        if (!isValidEmail(mailBoss)){
                            popUpMessage("המייל שהוזן שגוי");
                            return;
                        }
                        Job job = new Job(mailBoss,salary,currentUser.getMail(),jobName);
                        DB.setInJobs(job);
                        popUpMessage("המשרה נוספה בהצלחה");
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
    private LinearLayout getLinearLayoutOfAddJobs(EditText inputJobName, EditText inputBossMail, EditText inputSalaryForHour) {
        inputJobName.setHint("שם המשרה");
        inputSalaryForHour.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputSalaryForHour.setHint("שכר לשעה");
        inputBossMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputBossMail.setHint("מייל של המנהל");
        LinearLayout linearLayout= new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(inputJobName);
        linearLayout.addView(inputSalaryForHour);
        linearLayout.addView(inputBossMail);
        return linearLayout;
    }
    private boolean isValidInput(String input){
        return  (input.length() > 1);
    }
    private boolean isValidEmail(String mail) {
        return (!TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches());
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNewShift() {
//        popUpAddShiftWindow();
//        Shift shift = new Shift(getStart(), getEnd(), getMail(), getJobName());
//        DB.setInShifts(shift);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StartAndEndShift() {
//        if (ButtonIsStart()) {
//            Shift shift = new Shift(LocalDateTime.now(), null, getMail(), getJobName());
//            DB.setInShifts(shift);
//        } else updateEndOfShift();
    }
    private void sortShifts() {
    }
    private void updateEndOfShift() {
    }
    private boolean ButtonIsStart() {
        return false;
    }
    private String getJobName() {
        return "";
    }
    private String getMail() {
        return "";
    }
    private LocalDateTime getEnd() {
        return null;
    }
    private LocalDateTime getStart() {
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveShift() {
//        LocalDateTime start = LocalDateTime.of(2021,11,15,21,22);
//        LocalDateTime end = LocalDateTime.of(2021,11,15,21,22);
//        Shift shift = new Shift(null, null, "0", "");
//        DB.setInShifts(shift);
//        closeAddShiftWindow();
    }
    private void popUpAddShiftWindow() {
    }
    private void closeAddShiftWindow() {
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        signOutFromGoogle();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void signOutFromGoogle() {
        LoginActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(WorkerActivity.this, "good by", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void popUpMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}