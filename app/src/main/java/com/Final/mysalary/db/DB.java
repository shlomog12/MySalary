package com.Final.mysalary.db;



import static java.lang.Math.max;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.Final.mysalary.DTO.*;
import com.Final.mysalary.UI.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
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
        String path = user.getType()+"/";
        path += user.getUserName();
        setData(path + "/first_name",user.getFirst_name());
        setData(path + "/last_name",user.getLast_name());
        setData(path + "/mail",user.getMail());
        setData(path + "/password",user.getPassword());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setInShifts(Shift shift) {
        DatabaseReference dbRef = database.getReference("workers/"+shift.getUserName()+"/shifts");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int max =0;
                for (DataSnapshot child: snapshot.getChildren()) {
                    max = max(Integer.valueOf(child.getKey()),max);
                }
                int shift_id = max+1;
                String path = "workers/"+ shift.getUserName()+"/shifts/"+shift_id;
                setData(path+"/end",shift.getDateTimeEnd().toString());
                setData(path+"/start",shift.getDateTimeStart().toString());
                setData(path+"/job_id",String.valueOf(shift.getJobId()));
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });

        return;

    }
    public static void setInJobs(Job newJob) {
        DatabaseReference dbRef = database.getReference("workers/"+newJob.getUserNameWorker()+"/jobs");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int max =0;
                for (DataSnapshot child: snapshot.getChildren()) {
                    max = max(Integer.valueOf(child.getKey()),max);
                }
                int job_id = max+1;
                String path = "workers/"+ newJob.getUserNameWorker()+"/jobs/"+job_id;
                setData(path+"/salary",String.valueOf(newJob.getSalary()));
                setData(path+"/userNameBoss",newJob.getUserNameBoss());
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
        DatabaseReference dbRef = database.getReference("workers/"+userName+"/jobs/"+jobId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);

                callback.play(job.getSalary());
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });
        return;
    }


    public static void getShifts(LocalDateTime start, LocalDateTime end, String userNameWorker, Callback callback) {
        DatabaseReference dbRef = database.getReference("workers/"+userNameWorker+"/shifts");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Shift> shifts = new ArrayList<>();
                for (DataSnapshot child: snapshot.getChildren()){
                    Shift shift = child.getValue(Shift.class);
                    if (shift.getDateTimeStart().isAfter(start) && shift.getDateTimeEnd().isBefore(end)){
                        shifts.add(shift);
                    }
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


    private static void getBossByUserName(String userName, Callback callback) {
        DatabaseReference dbRef = database.getReference("bosses/"+userName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boss boss =snapshot.getValue(Boss.class);
                callback.play(boss);
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });
        return;
    }
    public static void getWorkerByUserName(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference("workers/"+userName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Worker worker =snapshot.getValue(Worker.class);
                callback.play(worker);
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });
        return;
    }


    public static void getUserByUserName(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference();
        dbRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot DBwokrer = snapshot.child("workers").child(userName);
                DataSnapshot DBboss = snapshot.child("bosses").child(userName);

                if (DBwokrer.exists()) {
                    Worker worker = DBwokrer.getValue(Worker.class);
                    worker.setUserName(userName);
                    callback.play(worker);
                    return;
                } else if (DBboss.exists()) {
                    Boss boss = DBboss.getValue(Boss.class);
                    boss.setUserName(userName);
                    callback.play(boss);
                    return;
                }
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                return;
            }
        });
        return;

    }

    public static void CheckIfTheUserNameIsBusy(String userName, Callback callback){
        DatabaseReference dbRef = database.getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot type: snapshot.getChildren()) {
                        for (DataSnapshot userFromDB: type.getChildren()){
                            if (userFromDB.getKey().equals(userName)){
                                callback.play(true);
                                return;
                            }
                        }
                }
                callback.play(false);
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

    }






}



//30652896089-12272i8lgqvqlbb6ber8rs16v2kig76j.apps.googleusercontent.com
//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