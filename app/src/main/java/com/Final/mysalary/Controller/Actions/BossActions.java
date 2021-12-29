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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.*;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.Job;
import com.Final.mysalary.Model.DTO.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BossActions extends UiActions{

    public BossActions(AppCompatActivity activity) {
        super(activity);
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
                            System.out.println("**************************************************************79");
                            System.out.println(StartDate);
                            System.out.println(EndDate);
                            System.out.println(mail);
                            System.out.println("**************************************************************79");
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
        showListOfShifts("");
    }

    private void filter_results(EditText StartDate, EditText EndDate, String mail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            shift_start = LocalDateTime.parse(StartDate.getText().toString() + " " + "00:00", formatter);
            shift_end = LocalDateTime.parse(EndDate.getText().toString() + " " + "23:59", formatter);


            System.out.println("**************************************************************140");
            System.out.println(shift_start);
            System.out.println(shift_end);
            System.out.println(mail);
            System.out.println("**************************************************************144");





            if (!Validate.isValidDateTime(shift_start, shift_end)) {
                System.out.println("**************************************************************151");
                popUpMessage(R.string.invalid_details);
                return;
            }
        } catch (Exception e) {
            System.out.println("**************************************************************156");
            popUpMessage(R.string.invalid_details);
            return;
        }
        System.out.println("**************************************************************160");
        super.showListOfShifts(mail);
    }

    public void updateUser() {
        String userMail = getUserMail();
        if (userMail == null) return;
        DB.getUserByUserMail(userMail, new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void play(User user) {
                currentUser = user;
                ChangeSum();
//                ChangeSum();
                showListOfShifts("");
            }
        });
    }

}
