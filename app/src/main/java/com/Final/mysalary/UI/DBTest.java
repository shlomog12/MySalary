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
    public static void test(String userName_) throws InterruptedException {
        userName = userName_;
//        testSetUser();
//        testSetJobs();
        testSetShifts();
//        testGetSalaryOfJob();
//        testGetShifts();
//        testGetUser();
//        testCheckIfTheUserNameIsExists();
//        testGetJobs();
    }

    private static void testGetJobs() {
        DB.getJobs(userName, new Callback<ArrayList<Job>>() {
            @Override
            public void play(ArrayList<Job> jobs) {
                System.out.println("************************************TEST**************************************8");
                for (Job job:jobs) {
                    System.out.println(job);
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
        DB.getShifts(1,  LocalDateTime.MIN ,LocalDateTime.MAX,userName, new Callback<ArrayList<Shift>>() {
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
    private static void testGetSalaryOfJob() {
        DB.getSalaryForJob(userName, 1, new Callback<String>() {
            @Override
            public void play(String salary) {
                System.out.println("************************************TEST**************************************8");
                double mys = Double.parseDouble(salary);
                System.out.println("salary = "+mys);
                System.out.println("**************************************************************************8");
            }
        });
    }
    private static void testSetUser() {
        User user = new User();
        user.setUserName(userName);
        user.setFirstName("test9_12");
        user.setLastName("test9_12");
        user.setPassword("0812*D9_12");
        user.setMail("test@812.com9_12");
        user.setType(Type.BOSS.ordinal());
        DB.setUser(user);
    }
    private static void testSetJobs() {
        Job job = new Job(userName,"avi8_12","812");
        DB.setInJobs(job);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testSetShifts() {
        Shift shift =new Shift(LocalDateTime.of(2021,12,9,20,55,44),LocalDateTime.of(2021,12,10,07,22,04),userName,0);
        DB.setInShifts(shift);




        Shift shift2 =new Shift(LocalDateTime.of(2020,11,30,20,55,44),LocalDateTime.of(2020,12,1,07,22,04),userName,1);
        DB.setInShifts(shift2);
    }

}