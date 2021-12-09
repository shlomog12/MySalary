package com.Final.mysalary.db;



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

    public static void setUser(User user) {
        DatabaseReference dbRefToUser = database.getReference().child(config.USERS).child(user.getUserName());
        dbRefToUser.child(config.FIRST_NAME).setValue(user.getFirstName());
        dbRefToUser.child(config.LAST_NAME).setValue(user.getLastName());
        dbRefToUser.child(config.MAIL).setValue(user.getMail());
        dbRefToUser.child(config.PASSWORD).setValue(user.getPassword());
        dbRefToUser.child(config.TYPE).setValue(user.getType().ordinal());
    }
    public static void setInJobs(Job newJob) {
        DatabaseReference dbRefToJobs = database.getReference().child(config.USERS).child(newJob.getUserNameWorker()).child(config.JOBS);
        dbRefToJobs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int jobId = (int) snapshot.getChildrenCount();
                DatabaseReference dbRefToJob = snapshot.getRef().child(String.valueOf(jobId));
                dbRefToJob.child(config.BOSS).setValue(newJob.getUserNameBoss());
                dbRefToJob.child(config.SALARY_FOR_HOUR).setValue(newJob.getSalaryForHour());
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setInShifts(Shift shift) {
        DatabaseReference dbRefToShiftsOfThisWorker = database.getReference().child(config.USERS).child(shift.getUserName()).child(config.JOBS).child(String.valueOf(shift.getJobId())).child(config.SHIFTS);
        dbRefToShiftsOfThisWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int shiftId = (int) snapshot.getChildrenCount();
                DatabaseReference dbRefToShift = snapshot.getRef().child(String.valueOf(shiftId));
                dbRefToShift.child(config.START).setValue(shift.getStart().toString());
                dbRefToShift.child(config.END).setValue(shift.getEnd().toString());
                dbRefToShift.child(config.JOB_ID).setValue(shift.getJobId());
//                dbRefToShift.child(config.JOB_ID).setValue(shift.getJobId());
//                dbRefToShift.child(config.TOTAL_SALARY).setValue(shift.getJobId());
//                DB.getSalaryForJob(shift.getUserName(), shift.getJobId(), new Callback() {
//                    @Override
//                    public void play(Object o) {
//                        dbRefToShift.child(config.TOTAL_SALARY).setValue(shift.getTotalSalary());
//                    }
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    public static void getSalaryForJob(String userName, int jobId, Callback callback) {
        DatabaseReference dbRef = database.getReference().child(config.USERS).child(userName).child(config.JOBS).child(String.valueOf(jobId));
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Job job = snapshot.getValue(Job.class);
                    callback.play(job.getSalaryForHour());
                    return;
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getShifts(int jobId,LocalDateTime start, LocalDateTime end, String userNameWorker, Callback callback) {
        DatabaseReference dbRef = database.getReference().child(config.USERS).child(userNameWorker).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Shift> shifts = new ArrayList<>();
                if (jobId == -1) shifts = getListOfAllShifts(start,end,snapshot,userNameWorker);
                else{
                    DataSnapshot snapshotOfJob = snapshot.child(String.valueOf(jobId));
                    shifts = getListOfShifts(start,end,snapshotOfJob,shifts,userNameWorker);
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
        for (DataSnapshot dataSnapshotJob : snapshot.getChildren()) {
//            Job job = dataSnapshotJob.getValue(Job.class);
//            String salaryForHour = job.getSalaryForHour();
//            DataSnapshot shiftsOfJob = dataSnapshotJob.child(config.SHIFTS);
            getListOfShifts(start,end,dataSnapshotJob,shifts, userNameWorker);
        }
        return shifts;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList getListOfShifts(LocalDateTime start, LocalDateTime end, DataSnapshot dataSnapshotJob, ArrayList shifts, String userNameWorker) {
        String salaryForHour = dataSnapshotJob.getValue(Job.class).getSalaryForHour();
        DataSnapshot shiftsOfJob = dataSnapshotJob.child(config.SHIFTS);
        for (DataSnapshot shiftFromDB : shiftsOfJob.getChildren()) {
            Shift shift = shiftFromDB.getValue(Shift.class);
            shift.setUserName(userNameWorker);
            shift.updateSalary(salaryForHour);
            if (shift.getStart().isAfter(start) && shift.getEnd().isBefore(end)) {
                shifts.add(shift);
            }
        }
        return shifts;
    }
    public static void getUserByUserName(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference(config.USERS).child(userName);
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
        DatabaseReference dbRef = database.getReference(config.USERS).child(userName);
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
        DatabaseReference dbRef = database.getReference(config.USERS).child(userName).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList <Job> jobs = new ArrayList<>();
                for (DataSnapshot jobFromData: snapshot.getChildren()) {
                    Job job =  jobFromData.getValue(Job.class);
                    job.setJobId(Integer.parseInt(jobFromData.getKey()));
                    job.setUserNameWorker(userName);
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



