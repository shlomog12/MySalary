package com.Final.mysalary.UI;


import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.Actions.WorkerActions;
import com.Final.mysalary.Model.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkerActivity extends AppCompatActivity {

    WorkerActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        actions = new WorkerActions(this);
    }

    public void onStart() {
        super.onStart();
        updateUser();
    }

    private void updateUser() {
        String userMail = actions.getUserMail();
        if (userMail == null) return;
        DB.getUserByUserMail(userMail, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                actions.setCurrentUser(user);
                actions.ChangeSum();
                System.out.println("**************************************************71");
                actions.showListOfShifts();
                System.out.println("**************************************************73");
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.worker_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_shift:
                actions.addNewShift();
                return true;
            case R.id.menu_add_job:
                actions.addJob();
                return true;
            case R.id.menu_logout:
                actions.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}