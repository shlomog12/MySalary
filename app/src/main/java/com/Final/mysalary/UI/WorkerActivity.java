package com.Final.mysalary.UI;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_1:
                Toast.makeText(this, "check 1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_2:
                Toast.makeText(this, "check 2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

//    public void onButtonShowPopupWindowClick(View view) {
//
////         inflate the layout of the popup window
//        LayoutInflater inflater = (LayoutInflater)
//                getSystemService(LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.popup_add_job, null);
//
////         create the popup window
//        int width = 360;
//        int height = 700;
//        boolean focusable = true; // lets taps outside the popup also dismiss it
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
////         show the popup window
////         which view you pass in doesn't matter, it is only used for the window token
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//
////         dismiss the popup window when touched
//        findViewById(R.id.PopUpExit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow.dismiss();
//            }
//        });
//    }

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


    private void addJob() {
//        openWindowAddJob();
//        Job newJob = new Job(getUserNameWarker(), getUserNameBoss(), "22","teacher");
//        DB.setInJobs(newJob);
//        closeWindowAddJob();
    }

    private int getJobIdFromScrean() {
        return 0;
    }

    private String getUserNameBoss() {
        return "";
    }

    private String getUserNameWarker() {
        return "";
    }

    private void closeWindowAddJob() {

    }

    private void openWindowAddJob() {
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
}