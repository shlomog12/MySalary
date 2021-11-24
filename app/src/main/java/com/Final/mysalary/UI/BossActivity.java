package com.Final.mysalary.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.Final.mysalary.DB;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.R;

import java.time.LocalDateTime;

public class BossActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
    }

    private void showShiftsLive() { }
    private void showShiftsByFilter(){
        Shift[] shifts = DB.getShifts(getStartFromFilter(),getEndFromFilter(),getUserNameFromFilter());
        showShifts(shifts);
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
    private void showShifts(Shift[] shifts) {
    }
}