package com.Final.mysalary.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.Final.mysalary.DTO.User;
import com.Final.mysalary.db.DB;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BossActivity extends AppCompatActivity {

    User currentUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
    }

    public void onStart() {
        super.onStart();
        updateUser();
    }

    private void updateUser() {
        String userName = updateUserName();
        if (userName == null) return;
        DB.getUserByUserName(userName, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                currentUser = user;
                start();
            }
        });
    }

    private void start() {
    }

    private String updateUserName() {
        String userName;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            userName = extras.getString("user");
            if (userName != null) return userName;
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        return userName;
    }


    private void showShiftsLive() { }
    private void showShiftsByFilter(){
    }

    private LocalDateTime getStartFromFilter() {
        return null;
    }
    private LocalDateTime getEndFromFilter() {
        return null;
    }
    private String getUserNameFromFilter() {
        return "";
    }
    private void cleanFilter(){ }
    private void showShifts(ArrayList<Shift> shifts) {
    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class));
    }
}