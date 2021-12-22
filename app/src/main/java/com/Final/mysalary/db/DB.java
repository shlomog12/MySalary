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
import java.util.Arrays;


public class DB {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void setUser(User user) {
        if (user == null) return;
        String userId =getSHA(user.getMail());
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
        DatabaseReference dbRef = database.getReference().child(config.USERS).child(getSHA(newJob.getMailWorker()))
                .child(config.JOBS).child(newJob.getJobName());
        dbRef.child(config.SALARY_FOR_HOUR).setValue(newJob.getSalaryForHour());
        dbRef.child(config.JOB_ID).setValue(jobId);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setInShifts(Shift shift) {
        if (shift == null) return;
        DatabaseReference dbRefToShiftsOfThisWorker = database.getReference().child(config.USERS)
                .child(getSHA(shift.UserMail())).child(config.JOBS)
                .child(String.valueOf(shift.JobName())).child(config.SHIFTS);
        // get all shifts
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
    public static void getShifts(String jobName,LocalDateTime start, LocalDateTime end, String userMailWorker, Callback callback) {
        if (userMailWorker ==null || start == null || end == null) return;
        DatabaseReference dbRef = database.getReference().child(config.USERS).child(getSHA(userMailWorker)).child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                ArrayList<Shift> shifts = new ArrayList<>();
                if (jobName == "") shifts = getListOfAllShifts(start,end,snapshot,userMailWorker);
                else{
                    DataSnapshot snapshotOfJob = snapshot.child(jobName);
                    shifts = getListOfShifts(start,end,snapshotOfJob,shifts,userMailWorker);

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
    private static ArrayList<Shift> getListOfAllShifts(LocalDateTime start, LocalDateTime end, DataSnapshot snapshot, String userMailWorker) {
        ArrayList shifts = new ArrayList();
        for (DataSnapshot dataSnapshotJob : snapshot.getChildren()) {
            getListOfShifts(start,end,dataSnapshotJob,shifts, userMailWorker);
        }
        return shifts;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList getListOfShifts(LocalDateTime start, LocalDateTime end, DataSnapshot dataSnapshotJob, ArrayList shifts, String userNameWorker) {
        String salaryForHour = dataSnapshotJob.getValue(Job.class).getSalaryForHour();
        String jobName = dataSnapshotJob.getKey();
        DataSnapshot shiftsOfJob = dataSnapshotJob.child(config.SHIFTS);
        for (DataSnapshot shiftFromDB : shiftsOfJob.getChildren()) {
            Shift shift = shiftFromDB.getValue(Shift.class);
            shift.updateSalary(salaryForHour);
            shift.setJobOfName(jobName);
            if (shift.Start().isAfter(start) && shift.End().isBefore(end)) {
                shifts.add(shift);
            }
        }
        return shifts;
    }
    public static void getUserByUserMail(String userMail, Callback callback){
        DatabaseReference dbRef = database.getReference(config.USERS).child(getSHA(userMail));
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.play(null);
                    return;
                }
                User user = snapshot.getValue(User.class);
                user.setMail(userMail);
                callback.play(user);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return;

    }
    public static void CheckIfTheUserMailIsExists(String userMail, Callback callback){
        DatabaseReference dbRef = database.getReference(config.USERS).child(getSHA(userMail));
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
    public static void getJobs(String userMail, Callback callback){
        DatabaseReference dbRef = database.getReference(config.USERS).child(getSHA(userMail)).child(config.JOBS);
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


    public static void getShiftsByBossMail(LocalDateTime start, LocalDateTime end, String userMailBoss, Callback callback) {
        if (userMailBoss ==null || start == null || end == null) return;
        DatabaseReference dbRef = database.getReference().child(config.JOBS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) return;
                    ArrayList <Job> jobs = new ArrayList<>();
                    for (DataSnapshot jobSnapshot :snapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job.getMailBoss().equals(userMailBoss)) {
                            jobs.add(job);
                        }
                    }
                getShiftsByJobs(start,end,jobs,callback);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private static void getShiftsByJobs(LocalDateTime start, LocalDateTime end, ArrayList<Job> jobs, Callback callback) {
        if (jobs.size() < 1 ) return;
        DatabaseReference dbRef = database.getReference().child(config.USERS);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                ArrayList<Shift> shifts = new ArrayList<>();
                for (Job job : jobs) {
                    DataSnapshot jobsSnapshot = snapshot.child(getSHA(job.getMailWorker())).child(config.JOBS)
                            .child(job.getJobName()).child(config.SHIFTS);
                    for (DataSnapshot shiftSnapshot: jobsSnapshot.getChildren()){
                        Shift shift = shiftSnapshot.getValue(Shift.class);
                        shift.setUserMail(job.getMailWorker());
                        if (shift.Start().isAfter(start) && shift.End().isBefore(end)) {
                            shifts.add(shift);
                        }
                    }
                }
                callback.play(shifts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }


}



