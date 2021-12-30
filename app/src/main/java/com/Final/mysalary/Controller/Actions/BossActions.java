package com.Final.mysalary.Controller.Actions;

import android.app.Dialog;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.*;
import com.Final.mysalary.Model.DTO.Shift;
import com.Final.mysalary.Model.DTO.Type;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.Job;
import com.Final.mysalary.Model.DTO.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BossActions extends UiActions{

    public BossActions(AppCompatActivity activity) {
        super(activity);
    }
    @Override
    public void showListOfShifts() {
        showListOfShifts("");
    }
    public void showSearch() {
        final Dialog dialog = new Dialog(activity);
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
                } else if (Validate.isValidEmail(mail)) {
                    DB.CheckIfTheUserMailIsExists(mail, new Callback<Boolean>() {
                        @Override
                        public void play(Boolean mailExist) {
                            if (!mailExist) {
                                popUpMessage(R.string.mail_not_exist);
                                return;
                            }
                            filter_results(StartDate, EndDate, mail);
                            dialog.dismiss();
                        }
                    });
                } else {
                    popUpMessage(R.string.mail_incorrect);
                }
                return;
            }
        });
        dialog.show();
    }
    private void updateDropDownWorkersMail(AutoCompleteTextView  WorkersMails) {
        DB.getJobsByBossMail(currentUser.getMail(),new Callback<ArrayList<Job>>() {
            @Override
            public void play(ArrayList<Job> jobs) {
                HashSet<String> Mails = new HashSet<>();
                for (Job job:jobs) {
                    Mails.add(job.getMailWorker());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (activity,android.R.layout.select_dialog_item, Mails.toArray(new String[0]));
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
    public void refresh() {
        shift_start = LocalDateTime.MIN;
        shift_end = LocalDateTime.MAX;
        showListOfShifts();
    }
    private void filter_results(EditText StartDate, EditText EndDate, String mail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            shift_start = LocalDateTime.parse(StartDate.getText().toString() + " " + "00:00", formatter);
            shift_end = LocalDateTime.parse(EndDate.getText().toString() + " " + "23:59", formatter);
            if (!Validate.isValidDateTime(shift_start, shift_end)) {
                popUpMessage(R.string.invalid_details);
                return;
            }
        } catch (Exception e) {
            popUpMessage(R.string.invalid_details);
            return;
        }
        showListOfShifts(mail);
    }
    protected void showListOfShifts(String mail) {
        if (currentUser == null) return;
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
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(activity, shift_of_worker, Type.BOSS);
                shiftsArrayAdapter.setShowSalary(ShowSalary);
                ListView shiftsListView = activity.findViewById(R.id.ShiftsForBoss);
                shiftsListView.setAdapter(shiftsArrayAdapter);
                TextView sum = activity.findViewById(R.id.TextBossSum);
                sum.setText(activity.getApplicationContext().
                        getString(R.string.sum_payment) + ": " + String.format("%.2f", totalsum[0])
                        + "\n" +
                        activity.getApplicationContext().getString(R.string.total_hours) + ": " + String.format("%.2f", totalHr[0]));
            }
        });
        return;
    }

}
