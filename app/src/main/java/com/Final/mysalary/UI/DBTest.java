package com.Final.mysalary.UI;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DBTest{

    static String userName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void test() throws InterruptedException {
        userName = "testDb4";
//        testSetUser();
//        testSetJobs();
//        testSetShifts();
//        testGetSalaryOfJob();
//        testGetShifts();
//        testGetUser();
//        testCheckIfTheUserNameIsExists();
//        testGetJobs();
    }

    private static void testGetJobs() {
        DB.getJobs(userName, new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobs) {
                System.out.println("************************************TEST**************************************8");
                for (String jobName:jobs) {
                    System.out.println(jobName);
                }
                System.out.println("**************************************************************************8");
            }
        });



    }
    private static void testCheckIfTheUserNameIsExists() {
        DB.CheckIfTheUserNameIsExists(userName, new Callback<Boolean>() {
            @Override
            public void play(Boolean isExits) {
                System.out.println("************************************TEST**************************************8");
                System.out.println(isExits);
                System.out.println("**************************************************************************8");
            }
        });

    }
    private static void testGetUser() {
        DB.getUserByUserName(userName, new Callback<User>() {
            @Override
            public void play(User user) {
                System.out.println("************************************TEST**************************************8");
                System.out.println(user);
                System.out.println("**************************************************************************8");
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testGetShifts() {

        System.out.println("***********************************70");
        DB.getShifts("",  LocalDateTime.MIN ,LocalDateTime.MAX,userName, new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                System.out.println("************************************TEST**************************************8");
                for (Shift shift: shifts) {
                    System.out.println(shift);
                }
                System.out.println("**************************************************************************8");
            }
        });


    }
//    private static void testGetSalaryOfJob() {
//        DB.getSalaryForJob(userName, 1, new Callback<String>() {
//            @Override
//            public void play(String salary) {
//                System.out.println("************************************TEST**************************************8");
//                double mys = Double.parseDouble(salary);
//                System.out.println("salary = "+mys);
//                System.out.println("**************************************************************************8");
//            }
//        });
//    }
    private static void testSetUser() {
        User user = new User();
        user.setUserName(userName);
        user.setFirstName("testDB9_12");
        user.setLastName("LAtestDB9_12");
        user.setPassword("A#0912");
        user.setMail("9_12@testDB.COM");
        user.setType(Type.WORKER.ordinal());
        DB.setUser(user);
    }
    private static void testSetJobs() {
        Job job = new Job("avi8_12","22",userName,"teacher");
        DB.setInJobs(job);
        Job job2 = new Job("avi8_12","200",userName,"programmer");
        DB.setInJobs(job2);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testSetShifts() {
        Shift shift =new Shift(LocalDateTime.of(2021,12,9,20,55,44),LocalDateTime.of(2021,12,10,07,22,04),userName,"programmer");
        DB.setInShifts(shift);




        Shift shift2 =new Shift(LocalDateTime.of(2020,11,30,20,55,44),LocalDateTime.of(2020,12,1,07,22,04),userName,"teacher");
        DB.setInShifts(shift2);
    }

}