package com.Final.mysalary;

import android.gesture.GestureOverlayView;

import com.Final.mysalary.DTO.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DB {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static void setTest(String path ,String test){
        Worker worker = new Worker("TEST@Gmail.com","moshe","1111","mosh44");
        setInWorkers(worker);



//        DatabaseReference myRef = database.getReference("test/"+path);
//        myRef.setValue(test);
//        System.out.println("setting");
    }

    public static void setInWorkers(Worker worker) {
        if (false) return;
        String userName = worker.getUserName();
        userName = "workers/"+userName;
        setData(userName+"/first_name",worker.getFirstName());
        setData(userName+"/last_name",worker.getLastName());
        setData(userName+"/mail",worker.getMail());
        setData(userName+"/password",worker.getPassword());
    }

    private static void setData(String path, String value) {
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(value);
    }





    public static void setInBosses(Boss boss) { }
    public static void setInShifts(Shift shift) { }
    public static void setInJobs(Job newJob) {
    }

    public static double getSalaryForJob(String userName, int jobId) { return 0; }
    public static Shift[] getShifts(LocalDateTime start, LocalDateTime end, String userNameWorker) {
        return null;
    }
    private static Boss getBossByUserName(String userName) {
        return null;
    }
    private static Worker getWorkerByUserName(String userName){
        return null;
    }

    public static boolean validNewUser(User user) {
        return false;
    }
}
