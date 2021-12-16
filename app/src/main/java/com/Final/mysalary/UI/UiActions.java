package com.Final.mysalary.UI;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.UI.date.DatePickerFragment;
import com.Final.mysalary.UI.date.TimePickerFragment;

public class UiActions {

    private final AppCompatActivity activity;

    public UiActions(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void moveToMainScreen(User curUser) {
        if (curUser == null) return;
        Intent intent;
        if (curUser.getType() == Type.WORKER.ordinal())
            intent = new Intent(activity, WorkerActivity.class);
        else intent = new Intent(activity, BossActivity.class);
        intent.putExtra("userMail", curUser.getMail());
        activity.startActivity(intent);
    }

    public void popUpMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public void popUpMessage(int message) {
        String messageStr = activity.getApplicationContext().getString(message);
        Toast.makeText(activity, messageStr, Toast.LENGTH_LONG).show();
    }
}
