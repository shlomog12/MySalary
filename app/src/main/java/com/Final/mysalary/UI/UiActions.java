package com.Final.mysalary.UI;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.UI.date.DatePickerFragment;
import com.Final.mysalary.UI.date.TimePickerFragment;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.Final.mysalary.notfication.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class UiActions {

    private final AppCompatActivity activity;

    public UiActions(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void moveToMainScreen(User curUser) {
        if (curUser == null) return;
        DB.updateToken(curUser.getMail());
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
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        sendNotificationToTokenId(tokenId,title,message+"from ALL");
    }

    private void sendNotificationToTokenId(String tokenId, String title, String message){
        System.out.println(tokenId);
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(tokenId,title
                ,message, activity.getApplicationContext(),activity);
        notificationsSender.SendNotifications();
    }

    public void sendNotificationToUserMail(String userMail, String title, String message){
        DB.getTokenIdByUserMail(userMail, new Callback<String>() {
            @Override
            public void play(String tokenId) {
                sendNotificationToTokenId(tokenId,title,message);
            }
        });
    }

    public String getMessgeNotification(String userName, String name, String hourPay) {
        String s1 ="המשתמש ";
        String s2 = " הוסיף אותך כמנהל למשרה ";
        String s3 = " בשכר של ";
        String s4 = " לשעה";
        return s1+userName+" "+s2+name + s3+hourPay + s4;
    }
}
