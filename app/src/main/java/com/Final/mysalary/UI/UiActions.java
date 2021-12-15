package com.Final.mysalary.UI;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;

public class UiActions {

    private final AppCompatActivity activity;

    public UiActions(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void moveToMainScreen(User curUser) {
        if (curUser == null) return;
        Intent intent;
        if (curUser.getType() == Type.WORKER.ordinal()) intent = new Intent(activity, WorkerActivity.class);
        else intent = new Intent(activity, BossActivity.class);
        intent.putExtra("userMail",  curUser.getMail());
        activity.startActivity(intent);
    }

    public void popUpMessage(String message) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
    }
}
