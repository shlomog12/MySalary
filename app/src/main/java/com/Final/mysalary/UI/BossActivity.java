package com.Final.mysalary.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Final.mysalary.db.DB;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BossActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
    }

    private void showShiftsLive() { }
    private void showShiftsByFilter(){
        DB.getShifts(0,getStartFromFilter(), getEndFromFilter(), getUserNameFromFilter(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                showShifts(shifts);
            }

        });
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