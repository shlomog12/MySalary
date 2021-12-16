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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.ShiftsAdapter;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.UI.date.DatePickerFragment;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BossActivity extends AppCompatActivity {

    User currentUser;
    FirebaseAuth mAuth;
    UiActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
        actions = new UiActions(this);
    }

    public void onStart() {
        super.onStart();
        updateUser();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.boss_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_boss_search:
                showSearch();
                return true;
            case R.id.menu_boss_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearch() {
        final Dialog dialog = new Dialog(BossActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_search_boss);
        final EditText workerMail = dialog.findViewById(R.id.SearchWorkerMail);
        final EditText StartDate = dialog.findViewById(R.id.SearchStartDate);
        final EditText EndDate = dialog.findViewById(R.id.SearchEndDate);
        Button submitButton = dialog.findViewById(R.id.btnBossSearch);
        updateDate(StartDate);
        updateDate(EndDate);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String mail = workerMail.getText().toString();
                DB.CheckIfTheUserMailIsExists(mail, new Callback<Boolean>() {
                    @Override
                    public void play(Boolean aBoolean) {
                        if (!aBoolean)
                            actions.popUpMessage(R.string.mail_not_exist);
                        actions.moveToMainScreen(currentUser);
                        return;
                    }
                });
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime shift_start;
                LocalDateTime shift_end;
                try {
                    shift_start = LocalDateTime.parse(StartDate.getText().toString() + " " + "00:00", formatter);
                    shift_end = LocalDateTime.parse(EndDate.getText().toString() + " " + "23:59", formatter);
                    if(!Validate.isValidDateTime(shift_start,shift_end)){
                        actions.popUpMessage("הנתונים שהוזנו אינם תקינים");
                        return;
                    }
                } catch (Exception e) {
                    actions.popUpMessage("הנתונים שהוזנו אינם תקינים");
                    return;
                }
                showListOfShifts(shift_start,shift_end,mail);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showListOfShifts(LocalDateTime start,LocalDateTime end,String mail) {
        if (currentUser == null) {
            return;
        }
        DB.getShifts("", start, end, mail, new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(BossActivity.this, shifts);
//                shiftsArrayAdapter.setShowSalary(ShowSalary);
                ListView shiftsListView = findViewById(R.id.listView);
                shiftsListView.setAdapter(shiftsArrayAdapter);
                double totalsum = 0;
                double totalHr = 0;
                for (Shift s : shifts) {
                    totalsum += s.TotalSalary();
                    totalHr += s.TotalHours();
                }
                TextView sum = findViewById(R.id.sumSalary);
                sum.setText(getApplicationContext().getString(R.string.sum_payment) + " " + String.format("%.2f", totalsum) + "\n" + getApplicationContext().getString(R.string.total_hours) + " " + String.format("%.2f", totalHr));
            }
        });
    }

    public void updateDate(EditText shiftDate) {
        DatePickerFragment date = new DatePickerFragment();
        shiftDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(date, shiftDate);
            }
        });
        shiftDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) showDate(date, shiftDate);
            }
        });
    }

    public void showDate(DatePickerFragment date, EditText shiftDate) {
        date.show(getSupportFragmentManager(), "datePicker");
        date.setEdit(shiftDate);
    }

    private void updateUser() {
        String userMail = getUserMail();
        if (userMail == null) return;
        DB.getUserByUserMail(userMail, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                currentUser = user;
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

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
}