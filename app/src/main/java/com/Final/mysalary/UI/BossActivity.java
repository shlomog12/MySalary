package com.Final.mysalary.UI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.ShiftsAdapter;
import com.Final.mysalary.Controller.UiActions;
import com.Final.mysalary.Controller.Validate;
import com.Final.mysalary.db.DTO.Job;
import com.Final.mysalary.db.DTO.Shift;
import com.Final.mysalary.db.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.Controller.date.DatePickerFragment;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BossActivity extends AppCompatActivity {

    User currentUser;
    FirebaseAuth mAuth;
    UiActions actions;
    LocalDateTime shift_start = LocalDateTime.MIN;
    LocalDateTime shift_end = LocalDateTime.MAX;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_boss_search:
                showSearch();
                return true;
            case R.id.menu_boss_logout:
                logout();
                return true;
            case R.id.refresh_shifts:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        shift_start=LocalDateTime.MIN;
        shift_end=LocalDateTime.MAX;
        showListOfShifts("");
    }

    private void showSearch() {
        final Dialog dialog = new Dialog(BossActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_search_boss);
        final AutoCompleteTextView AutoTextWorkerMail =(AutoCompleteTextView) dialog.findViewById(R.id.SearchWorkerMail);
        final EditText StartDate = dialog.findViewById(R.id.SearchStartDate);
        final EditText EndDate = dialog.findViewById(R.id.SearchEndDate);
        Button submitButton = dialog.findViewById(R.id.btnBossSearch);
        updateDate(StartDate);
        updateDate(EndDate);
        updateDropDownWorkersMail(AutoTextWorkerMail);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String mail = AutoTextWorkerMail.getText().toString();
                if (mail.equals("")) {
                    filter_results(StartDate, EndDate, mail);
                } else if(Validate.isValidEmail(mail)){
                    DB.CheckIfTheUserMailIsExists(mail, new Callback<Boolean>() {
                        @Override
                        public void play(Boolean mailExist) {
                            if (!mailExist) {
                                actions.popUpMessage(R.string.mail_not_exist);
                                return;
                            }
                            filter_results(StartDate, EndDate, mail);
                            dialog.dismiss();
                        }
                    });
                }
                else {
                    actions.popUpMessage(R.string.mail_incorrect);
                }
                return;
            }
        });
        dialog.show();
    }

    private void updateDropDownWorkersMail(AutoCompleteTextView WorkersMails) {
        DB.getJobsByBossMail(currentUser.getMail(),new Callback<ArrayList<Job>>() {
            @Override
            public void play(ArrayList<Job> jobs) {
                HashSet<String> Mails = new HashSet<>();
                for (Job job:jobs) {
                    Mails.add(job.getMailWorker());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (BossActivity.this,android.R.layout.select_dialog_item, Mails.toArray(new String[0]));
                WorkersMails.setAdapter(adapter);
                WorkersMails.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            WorkersMails.showDropDown();
                    }
                });
                WorkersMails.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        WorkersMails.showDropDown();
                        return false;
                    }
                });
            }
        });
    }

    private void filter_results(EditText StartDate, EditText EndDate, String mail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            shift_start = LocalDateTime.parse(StartDate.getText().toString() + " " + "00:00", formatter);
            shift_end = LocalDateTime.parse(EndDate.getText().toString() + " " + "23:59", formatter);
            if (!Validate.isValidDateTime(shift_start, shift_end)) {
                actions.popUpMessage(R.string.invalid_details);
                return;
            }
        } catch (Exception e) {
            actions.popUpMessage(R.string.invalid_details);
            return;
        }
        showListOfShifts(mail);
    }

    private void showListOfShifts(String mail) {
        if (currentUser == null) {
            return;
        }
        final double[] totalsum = {0};
        final double[] totalHr = {0};
        ArrayList<Shift> shift_of_worker = new ArrayList<>();
        DB.getShiftsByBossMail(shift_start, shift_end, currentUser.getMail(), new Callback<ArrayList<Shift>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(ArrayList<Shift> shifts) {
                for (Shift s : shifts) {
                    if (s.UserMail().equals(mail) || mail.equals("")) {
                        shift_of_worker.add(s);
                        totalsum[0] += s.TotalSalary();
                        totalHr[0] += s.TotalHours();
                    }
                }
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(BossActivity.this, shift_of_worker);
                //                    shiftsArrayAdapter.setShowSalary(ShowSalary);
                ListView shiftsListView = findViewById(R.id.ShiftsForBoss);
                shiftsListView.setAdapter(shiftsArrayAdapter);
                TextView sum = findViewById(R.id.TextBossSum);
                sum.setText(getApplicationContext().
                        getString(R.string.sum_payment) + ": " + String.format("%.2f", totalsum[0])
                        + "\n" +
                        getApplicationContext().getString(R.string.total_hours) + ": " + String.format("%.2f", totalHr[0]));
            }
        });
        return;
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
                showListOfShifts("");
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
        signOutFromGoogle();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    public void signOutFromGoogle() {
        LoginActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        actions.popUpMessage(R.string.bye);
                    }
                });
    }
}