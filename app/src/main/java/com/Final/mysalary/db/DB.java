package com.Final.mysalary.db;



import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.Final.mysalary.DTO.*;


import com.google.common.hash.Hashing;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class DB {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static void setUser(User user) {
        if (user == null) return;
        String userId =getSHA(user.getUserName());
        database.getReference().child(config.USERS).child(userId).setValue(user);
    }
    private static String getSHA(String input) {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }
    public static void setInJobs(Job newJob) {
        DatabaseReference dbRefToJobs = database.getReference().child(config.JOBS);
        dbRefToJobs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int jobId = (int) snapshot.getChildrenCount();
                DatabaseReference dbRefToJob = snapshot.getRef().child(String.valueOf(jobId));
                dbRefToJob.setValue(newJob);
                setJObWithTheUser(newJob,jobId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private static void setJObWithTheUser(Job newJob, int jobId) {
        DatabaseReference dbRef = database.getReference().child(config.USERS).child(getSHA(newJob.getUserNameWorker()))
                .child(config.JOBS).child(newJob.JobName());
        dbRef.child(config.SALARY_FOR_HOUR).setValue(newJob.getSalaryForHour());
        dbRef.child(config.JOB_ID).setValue(jobId);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setInShifts(Shift shift) {
        if (shift == null) return;
        DatabaseReference dbRefToShiftsOfThisWorker = database.getReference().child(config.USERS).child(getSHA(shift.UserName())).child(config.JOBS).child(String.valueOf(shift.JobName())).child(config.SHIFTS);
        dbRefToShiftsOfThisWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int shiftId = (int) snapshot.getChildrenCount();
                DatabaseReference dbRefToShift = snapshot.getRef().child(String.valueOf(shiftId));
                dbRefToShift.setValue(shift);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


//    public static void getSalaryForJob(String userName, int jobId, Callback callback) {
//        DatabaseReference dbRef = database.getReference().child(config.USERS).child(userName).child(config.JOBS).child(String.valueOf(jobId));
//        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Job job = snapshot.getValue(Job.class);
//                    callback.play(job.getSalaryForHour());
//                    return;
//                }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//
//            }
//        });
//    }


    public static void getShifts(String jobName,LocalDateTime start, LocalDateTime end, String userNameWorker, Callback callback) {
        if (userNameWorker ==null || start == null || end == null) return;
        DatabaseReference dbRef = database.getReference().child(config.USERS).child(getSHA(userNameWorker)).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                    ArrayList<Shift> shifts = new ArrayList<>();
                if (jobName == "") shifts = getListOfAllShifts(start,end,snapshot,userNameWorker);
                else{
                    DataSnapshot snapshotOfJob = snapshot.child(jobName);
                    shifts = getListOfShifts(start,end,snapshotOfJob,shifts,userNameWorker);

                }
                callback.play(shifts);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
        return;
    }









    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<Shift> getListOfAllShifts(LocalDateTime start, LocalDateTime end, DataSnapshot snapshot, String userNameWorker) {
        ArrayList shifts = new ArrayList();
        for (DataSnapshot dataSnapshotJob : snapshot.getChildren()) {
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
//            shift.setUserName(userNameWorker);
            shift.updateSalary(salaryForHour);
            if (shift.Start().isAfter(start) && shift.End().isBefore(end)) {
                shifts.add(shift);
            }
        }
        return shifts;
    }

    public static void getUserByUserName(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference(config.USERS).child(getSHA(userName));
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
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
        DatabaseReference dbRef = database.getReference(config.USERS).child(getSHA(userName));

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callback.play(snapshot.exists());
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error = " + error.toString());
            }
        });



        return;
    }


    public static void getJobs(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference(config.USERS).child(getSHA(userName)).child(config.JOBS);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                ArrayList <String> jobs = new ArrayList<>();
                for (DataSnapshot jobFromData: snapshot.getChildren()) {
                    String jobName = jobFromData.getKey();
                    jobs.add(jobName);
                }
                callback.play(jobs);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
    }


}



