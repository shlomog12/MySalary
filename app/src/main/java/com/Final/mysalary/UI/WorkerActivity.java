package com.Final.mysalary.UI;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.ShiftsAdapter;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WorkerActivity extends AppCompatActivity {

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
        if (extras != null) {
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
                addNewShift();
                return true;
            case R.id.menu_2:
                addJob();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Function to display the custom dialog.
    private void addJob() {
        final Dialog dialog = new Dialog(WorkerActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.popup_add_job);

        //Initializing the views of the dialog.
        final EditText jobName = dialog.findViewById(R.id.editJobName);
        final EditText hourSal = dialog.findViewById(R.id.editHourSal);
        final EditText bossId = dialog.findViewById(R.id.editBossMail);
        Button submitButton = dialog.findViewById(R.id.PopUpSave);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = jobName.getText().toString();
                String hourpay = hourSal.getText().toString();
                String bossMail = bossId.getText().toString();
                System.out.println("NAME: "+name+" HOUR PAY: "+hourpay+" bossMail: "+bossMail);
                Job job = new Job(bossMail, hourpay, currentUser.getMail(), name);
                DB.setInJobs(job);
                Toast.makeText(WorkerActivity.this, "משרה נוספה בהצלחה", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showTotalSumOfSalary() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showListOfShifts() {
        if (currentUser == null) {
            return;
        }
        DB.getShifts("", LocalDateTime.MIN, LocalDateTime.MAX, currentUser.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(WorkerActivity.this, shifts);
                ListView shiftsListView = findViewById(R.id.listView);
                shiftsListView.setAdapter(shiftsArrayAdapter);
                double totalsum=0;
                double totalHr=0;
                for (Shift s:shifts) {
                    totalsum+=s.TotalSalary();
                    totalHr+=s.TotalHours();
                }
                TextView sum = findViewById(R.id.sumSalary);
                sum.setText("סך כל השכר: "+String.format("%.2f",totalsum)+"\n"+"סך כל השעות: "+String.format("%.2f",totalHr));
            }
        });
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

    private void addNewShift() {
        final Dialog dialog = new Dialog(WorkerActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.popup_add_shift);

        //Initializing the views of the dialog.
        final EditText jobName = dialog.findViewById(R.id.editShiftName);
        final EditText ShiftStartDate = dialog.findViewById(R.id.editShiftStartDate);
        final EditText ShiftStart = dialog.findViewById(R.id.editShiftStart);
        final EditText ShiftEndDate = dialog.findViewById(R.id.editShiftEndDate);
        final EditText ShiftEnd = dialog.findViewById(R.id.editShiftEnd);
        Button submitButton = dialog.findViewById(R.id.buttonShiftSave);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = jobName.getText().toString();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                //convert String to LocalDate
                LocalDateTime shift_start = LocalDateTime.parse(ShiftStartDate.getText().toString()+" "+ShiftStart.getText().toString(), formatter);

                //convert String to LocalDate
                LocalDateTime shift_end = LocalDateTime.parse(ShiftEndDate.getText().toString()+" "+ShiftEnd.getText().toString(), formatter);

                Shift shift = new Shift(shift_start,shift_end,currentUser.getMail(),name);
                DB.setInShifts(shift);
                Toast.makeText(WorkerActivity.this, "משמרת נוספה בהצלחה", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
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

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        signOutFromGoogle();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void signOutFromGoogle() {
        LoginActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(WorkerActivity.this, "להתראות!!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}