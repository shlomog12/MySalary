package com.Final.mysalary.UI;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.notfication.FcmNotificationsSender;
import com.google.firebase.messaging.FirebaseMessaging;

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

    public void sendNotificationToAllUsers(String title, String message){
        String tokenId = "/topics/all";
        sendNotificationToTokenId(tokenId,title,message);
    }

    public void sendNotificationToTokenId(String tokenId, String title, String message){
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(tokenId,title
                ,message, activity.getApplicationContext(),activity);
        notificationsSender.SendNotifications();
    }
}
