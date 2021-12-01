package com.Final.mysalary.db;




//import static com.Final.mysalary.db.config.;
import static java.lang.Math.max;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.Final.mysalary.DTO.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DB {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static void setData(String path, String value) {
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(value);
    }
    public static void setUser(User user) {
        String path = user.getUserName()+"/";
        setData(path + config.FIRST_NAME,user.getFirstName());
        setData(path + config.LAST_NAME,user.getLastName());
        setData(path + config.MAIL,user.getMail());
        setData(path + config.PASSWORD,user.getPassword());
        setData(path + config.TYPE, String.valueOf(user.getType()));
    }
    public static void setInJobs(Job newJob) {
        DatabaseReference dbRef = database.getReference().child(newJob.getUserNameWorker()).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int job_id = (int) snapshot.getChildrenCount();
                DatabaseReference dbRefOfJob = snapshot.getRef().child(String.valueOf(job_id));
                dbRefOfJob.child(config.SALARY_FOR_HOUR).setValue(String.valueOf(newJob.getSalaryForHour()));
                dbRefOfJob.child(config.USER_NAME_BOSS).setValue(String.valueOf(newJob.getUserNameBoss()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setInShifts(Shift shift) {
        DatabaseReference dbRef = database.getReference().child(shift.getUserName()).child(config.JOBS)
                .child(String.valueOf(shift.getJobId())).child(config.SHIFTS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int shift_id = (int) snapshot.getChildrenCount();
                        DatabaseReference dbRefOfShit = snapshot.getRef().child(String.valueOf(shift_id));
                        dbRefOfShit.child(config.END).setValue(shift.getEnd().toString());
                        dbRefOfShit.child(config.START).setValue(shift.getStart().toString());
                        dbRefOfShit.child(config.JOB_ID).setValue(String.valueOf(shift.getJobId()));
                        DB.getSalaryForJob(shift.getUserName(), shift.getJobId(), new Callback<String>() {
                            @Override
                            public void play(String s) {
                                dbRefOfShit.child(config.TOTAL_SALARY).setValue(shift.getTotalSalary());
                            }

                        });
                        return;
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error){
                        return;
                    }
                });

        return;

    }
    public static void getSalaryForJob(String userName, int jobId, Callback callback) {
        DatabaseReference dbRef = database.getReference().child(userName).child(config.JOBS).child(String.valueOf(jobId));
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);
                callback.play(job.getSalaryForHour());
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });
        return;
    }
    public static void getShifts(int jobId,LocalDateTime start, LocalDateTime end, String userNameWorker, Callback callback) {
        DatabaseReference dbRef = database.getReference().child(userNameWorker).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Shift> shifts = new ArrayList<>();
                if (jobId == -1) shifts = getListOfAllShifts(start,end,snapshot,userNameWorker);
                else{
                    DataSnapshot shiftsOfJob = snapshot.child(String.valueOf(jobId)).child(config.SHIFTS);
                    shifts = getListOfShifts(start,end,shiftsOfJob,shifts,userNameWorker);
                }
                callback.play(shifts);
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });

        return;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<Shift> getListOfAllShifts(LocalDateTime start, LocalDateTime end, DataSnapshot snapshot,String userNameWorker) {
        ArrayList shifts = new ArrayList();
        for (DataSnapshot job : snapshot.getChildren()) {
            DataSnapshot shiftsOfJob = job.child(config.SHIFTS);
            getListOfShifts(start,end,shiftsOfJob,shifts, userNameWorker);
        }
        return shifts;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList getListOfShifts(LocalDateTime start, LocalDateTime end, DataSnapshot shiftsOfJob, ArrayList shifts, String userNameWorker) {
        for (DataSnapshot shiftFromDB : shiftsOfJob.getChildren()) {
            Shift shift = shiftFromDB.getValue(Shift.class);
            shift.setUserName(userNameWorker);
            shift.updateTotalSalary();
            if (shift.getStart().isAfter(start) && shift.getEnd().isBefore(end)) {
                shifts.add(shift);
            }
        }
        return shifts;
    }
    public static void getUserByUserName(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference(userName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callback.play(null);
                    return;
                }
                User user = snapshot.getValue(User.class);
                    user.setUserName(userName);
                    callback.play(user);
                    return;
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return;

    }
    public static void CheckIfTheUserNameIsExists(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference().child(userName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callback.play(snapshot.exists());
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error = "+error.toString());
            }
        });
        return;
    }

    public static void getJobs(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference().child(userName).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList <Job> jobs = new ArrayList<>();
                for (DataSnapshot jobFromData: snapshot.getChildren()) {
                    Job job =  jobFromData.getValue(Job.class);
                    jobs.add(job);
                }
                callback.play(jobs);
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });
    }




}



