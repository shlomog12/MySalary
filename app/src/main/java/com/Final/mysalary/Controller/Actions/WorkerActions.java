package com.Final.mysalary.Controller.Actions;

import static java.lang.Thread.sleep;

import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.*;
import com.Final.mysalary.Controller.date.DatePickerFragment;
import com.Final.mysalary.Controller.date.TimePickerFragment;
import com.Final.mysalary.Model.DTO.Type;
import com.Final.mysalary.Model.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.Job;
import com.Final.mysalary.Model.DTO.Shift;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkerActions extends UiActions {

    boolean isLive = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public WorkerActions(AppCompatActivity activity) {
        super(activity);
    }

    public void addNewShift() {
        DB.getJobs(currentUser.getMail(), new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobs) {
                addNewShiftToJobFromJobs(jobs, null);
            }
        });
    }

    public void addNewShiftToJobFromJobs(ArrayList<String> jobs, Shift oldShift) {
        final Dialog dialog = new Dialog(activity);

        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_add_shift);
        final AutoCompleteTextView AutoTextJobName = (AutoCompleteTextView) dialog.findViewById(R.id.editShiftName);
        final EditText shiftStartDate = dialog.findViewById(R.id.editShiftStartDate);
        final EditText shiftTimeStart = dialog.findViewById(R.id.editShiftStartTime);
        final EditText shiftEndDate = dialog.findViewById(R.id.editShiftEndDate);
        final EditText shiftTimeEnd = dialog.findViewById(R.id.editShiftEndTime);
        updateInput(shiftStartDate, shiftTimeStart, shiftEndDate, shiftTimeEnd, oldShift, AutoTextJobName);
        updateDropDownJobsName(AutoTextJobName);
        Button submitButton = dialog.findViewById(R.id.btnNewShiftSave);
        updateDateTime(shiftStartDate, shiftTimeStart, shiftEndDate, shiftTimeEnd);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = AutoTextJobName.getText().toString();
                if (!jobs.contains(name)) {
                    popUpMessage(R.string.invalid_job);
                    return;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                try {
                    LocalDateTime shift_start = LocalDateTime.parse(shiftStartDate.getText().toString() + " " + shiftTimeStart.getText().toString(), formatter);
                    LocalDateTime shift_end = LocalDateTime.parse(shiftEndDate.getText().toString() + " " + shiftTimeEnd.getText().toString(), formatter);
                    if (shift_end.isBefore(shift_start)) {
                        popUpMessage(R.string.invalid_details);
                        return;
                    }
                    if (Validate.isValidDateTime(shift_start, shift_end)) {
                        DB.removeShift(oldShift);
                        String mailUser = (currentUser != null) ? currentUser.getMail() : oldShift.UserMail();
                        Shift shift = new Shift(shift_start, shift_end, mailUser, name);
                        DB.setInShifts(shift);
                        popUpMessage(R.string.shift_added_successfully);
                        dialog.dismiss();
                    } else throw new Exception();
                } catch (Exception e) {
                    popUpMessage(R.string.invalid_details);
                    return;
                }
            }
        });
        dialog.show();
    }

    public void LiveShift() {
        if (isLive)
            StartNewLiveShift();
        else EndNewLiveShift();
    }

    public void StartNewLiveShift() {
        final Dialog dialog = new Dialog(activity);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_live_shift);
        TextView start_end = activity.findViewById(R.id.start_live_shift);
        final AutoCompleteTextView AutoTextJobName = (AutoCompleteTextView) dialog.findViewById(R.id.editLiveShiftName);
        updateDropDownJobsName(AutoTextJobName);
        Button submitButton = dialog.findViewById(R.id.btnDialogLiveShift);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = AutoTextJobName.getText().toString();
                Shift shift = new Shift(LocalDateTime.now(), LocalDateTime.MAX, currentUser.getMail(), name);
                DB.setInShifts(shift);
                popUpMessage(R.string.live_shift_started);
                start_end.setText(activity.getApplicationContext().getString(R.string.end_live));
                isLive = false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void EndNewLiveShift() {
        DB.getShifts("", LocalDateTime.MIN, LocalDateTime.MAX, currentUser.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                for (Shift shift : shifts) {
                    if (shift.End().equals(LocalDateTime.MAX)) {
                        shift.setEndTime(LocalDateTime.now());
                        DB.removeShift(shift);
                        DB.setInShifts(shift);
                        popUpMessage(R.string.live_shift_ended);
                        TextView start_end = activity.findViewById(R.id.start_live_shift);
                        start_end.setText(activity.getApplicationContext().getString(R.string.start_live));
                        isLive = true;
                        break;
                    }
                }
            }
        });
        return;
    }

    private void updateInput(EditText startDate, EditText StartTime, EditText endDate, EditText endTime, Shift shift, AutoCompleteTextView jobName) {
        if (shift == null) return;
        LocalDateTime startShift = LocalDateTime.parse(shift.getStart());
        LocalDateTime endShift = LocalDateTime.parse(shift.getEnd());

        String strStartDate = DatePickerFragment.getStrOfDate(startShift.getYear(), startShift.getMonthValue() - 1, startShift.getDayOfMonth());
        String strEndDate = DatePickerFragment.getStrOfDate(endShift.getYear(), endShift.getMonthValue() - 1, endShift.getDayOfMonth());
        String strStartTime = TimePickerFragment.getStringFromTime(startShift.getHour(), startShift.getMinute());
        String strEndTime = TimePickerFragment.getStringFromTime(endShift.getHour(), endShift.getMinute());

        startDate.setText(strStartDate, TextView.BufferType.EDITABLE);
        StartTime.setText(strStartTime, TextView.BufferType.EDITABLE);
        endTime.setText(strEndTime, TextView.BufferType.EDITABLE);
        endDate.setText(strEndDate, TextView.BufferType.EDITABLE);
        jobName.setText(shift.JobName(), TextView.BufferType.EDITABLE);
    }

    private void updateDropDownJobsName(AutoCompleteTextView jobName) {
        if (currentUser == null) return;
        DB.getJobs(currentUser.getMail(), new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobsNames) {
                String[] jobsName = new String[jobsNames.size()];
                for (int i = 0; i < jobsNames.size(); i++) {
                    jobsName[i] = jobsNames.get(i);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (activity, android.R.layout.select_dialog_item, jobsName);
                jobName.setAdapter(adapter);
                jobName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            jobName.showDropDown();
                    }
                });
                jobName.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        jobName.showDropDown();
                        return false;
                    }
                });
            }
        });

    }

    public void addJob() {
        final Dialog dialog = new Dialog(activity);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_add_job);
        final EditText jobName = dialog.findViewById(R.id.editJobName);
        final EditText hourSal = dialog.findViewById(R.id.editHourPay);
        final EditText bossId = dialog.findViewById(R.id.editBossMail);
        Button submitButton = dialog.findViewById(R.id.btnNewJobSave);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = jobName.getText().toString();
                if (!Validate.isValidInput(name)) {
                    popUpMessage(R.string.invalid_job);
                    return;
                }
                String hourPay = hourSal.getText().toString();
                if (!Validate.isNumeric(hourPay)) {
                    popUpMessage(R.string.invalid_salary);
                    return;
                }
                String bossMail = bossId.getText().toString();
                if (!Validate.isValidEmail(bossMail)) {
                    popUpMessage(R.string.boss_mail_wrong);
                    return;
                }
                DB.CheckIfTheUserMailIsExists(bossMail, new Callback<Boolean>() {
                    @Override
                    public void play(Boolean isExits) {
                        if (!isExits) {
                            popUpMessage(R.string.boss_mail_wrong);
                            return;
                        }
                        Job job = new Job(bossMail, hourPay, currentUser.getMail(), name);
                        String title = activity.getApplicationContext().getString(R.string.user_added);
                        String message = getMessageNotification(currentUser.getUserName(), name, hourPay);
                        sendNotificationToUserMail(bossMail, title, message);
                        DB.setInJobs(job);
                        popUpMessage(R.string.job_added_success);
//                        showListOfShifts();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    public void setEditAndRemove(View editShift, Shift currentShift) {
        DB.getUserByUserMail(currentShift.UserMail(), new Callback<User>() {
            @Override
            public void play(User user) {
                currentUser = user;
                openDialogEditAndRemove(editShift, currentShift);
            }
        });
    }

    private void openDialogEditAndRemove(View editShift, Shift currentShift) {
        editShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(editShift.getContext()).create();
                alert.setButton(Dialog.BUTTON_NEUTRAL, activity.getApplicationContext().getString(R.string.edit_shift), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showEditShift(currentShift);
                    }
                });
                alert.setButton(Dialog.BUTTON_NEGATIVE, activity.getApplicationContext().getString(R.string.delete_shift), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialogRemoveShift(editShift, currentShift);
                    }
                });
                alert.show();
            }
        });
    }

    private void showEditShift(Shift currentShift) {
        ArrayList<String> jobsNames = new ArrayList<>();
        jobsNames.add(currentShift.JobName());
        addNewShiftToJobFromJobs(jobsNames, currentShift);
    }

    private void showDialogRemoveShift(View editShift, Shift currentShift) {
        AlertDialog alert = new AlertDialog.Builder(editShift.getContext()).create();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                showListOfShifts();
            }
        });
        alert.setTitle(activity.getApplicationContext().getString(R.string.sure_delete));
        alert.setButton(Dialog.BUTTON_POSITIVE, activity.getApplicationContext().getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DB.removeShift(currentShift);
                alert.dismiss();
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, activity.getApplicationContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    public void showListOfShifts() {
        if (currentUser == null) {
            return;
        }
        DB.getShifts("", LocalDateTime.MIN, LocalDateTime.MAX, currentUser.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(activity, shifts, Type.WORKER);
                shiftsArrayAdapter.setShowSalary(ShowSalary);
                ListView shiftsListView = activity.findViewById(R.id.listView);
                shiftsListView.setAdapter(shiftsArrayAdapter);
                double totalsum = 0;
                double totalHr = 0;
                for (Shift s : shifts) {
                    if (!s.End().equals(LocalDateTime.MAX)) {
                        totalsum += s.TotalSalary();
                        totalHr += s.TotalHours();
                    }
                }
                TextView sum = activity.findViewById(R.id.sumSalary);
                sum.setText(activity.getApplicationContext().getString(R.string.sum_payment) + " " + String.format("%.2f", totalsum) + "\n" + activity.getApplicationContext().getString(R.string.total_hours) + " " + String.format("%.2f", totalHr));
            }
        });
    }

}
