package com.Final.mysalary;

import android.gesture.GestureOverlayView;
import android.os.Build;

import androidx.annotation.RequiresApi;

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


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void setTest(String path , String test){
////        Worker worker = new Worker("TEST@Gmail.com","moshe","d","1111","mosh44");
////        setInWorkers(worker);
////        Shift shift = new Shift(LocalDateTime.now(),LocalDateTime.now(),"shlomo123",2);
////        setInShifts(shift);
//    }

    public static void setInWorkers(Worker worker) {
        if (false) return;
        setUser("workers/",worker);
    }
    public static void setInBosses(Boss boss) {
        if (false) return;
        setUser("bosses/",boss);
    }


    private static void setUser(String path,User user) {
        path += user.getUserName();
        setData(path + "/first_name",user.getFirstName());
        setData(path + "/last_name",user.getLastName());
        setData(path + "/mail",user.getMail());
        setData(path + "/password",user.getPassword());
    }
    private static void setData(String path, String value) {
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(value);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setInShifts(Shift shift) {
        int shift_id = getMaxIdOfShifts();
        String path = "workers/"+ shift.getUserName()+"/shifts/"+shift_id;
        setData(path+"/end",shift.getDateTimeEnd().toString());
        setData(path+"/start",shift.getDateTimeStart().toString());
        setData(path+"/job_id",String.valueOf(shift.getJobId()));
    }
    public static void setInJobs(Job newJob) {
        int job_id = getMaxIdOfJobs();
        String path = "workers/"+ newJob.getUserNameWorker()+"/jobs/"+job_id;
        setData(path+"/salary",String.valueOf(newJob.getSalary()));
        setData(path+"/userNameBoss",newJob.getUserNameBoss());
    }




    private static int getMaxIdOfShifts() {
        return 0;
    }
    private static int getMaxIdOfJobs() {
        return 0;
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
